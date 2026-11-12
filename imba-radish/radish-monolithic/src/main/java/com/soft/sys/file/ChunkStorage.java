package com.soft.sys.file;

import com.soft.common.exception.GlobalException;
import com.soft.sys.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 分片上传的本地临时存储。
 *
 * <p>目录结构：{@code ${tmp.path}/upload-chunks/{userId}/{fileMd5}/}，这样做有三个目的：</p>
 * <ul>
 *     <li>用户隔离：同 hash 的文件不会在不同用户之间串分片；</li>
 *     <li>独立根目录：定时清理只扫 upload-chunks，不会误删 tmp 下其它临时文件；</li>
 *     <li>原子写：分片先写 .part 再 move，中断/并发都不会留下半截分片被当成"已上传"。</li>
 * </ul>
 *
 * <p>浏览器被关闭时不会有任何回调能通知服务端，因此残留分片依赖 {@code ChunkCleanupJob}
 * 按最后活动时间做 TTL 兜底回收，而不是靠前端。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChunkStorage {

    /** 分片根目录名，独立于 tmp.path 下的其它临时文件 */
    public static final String ROOT_DIR = "upload-chunks";

    /** 合并产物固定文件名：绝不把前端传来的 fileName 当作磁盘路径，杜绝路径穿越 */
    public static final String MERGED_FILE = "merged.tmp";

    private static final String PART_SUFFIX = ".part";
    private static final Pattern HASH_PATTERN = Pattern.compile("^[a-fA-F0-9]{32}$");
    private static final Pattern INDEX_PATTERN = Pattern.compile("^\\d+$");

    /** 分段锁，避免为每一个 md5 永久保存一个锁对象 */
    private static final int LOCK_STRIPES = 64;
    private final Object[] locks = createLocks();

    private final SecurityUtil securityUtil;

    @Value("${tmp.path}")
    private String tmpPath;

    private static Object[] createLocks() {
        Object[] stripes = new Object[LOCK_STRIPES];
        for (int i = 0; i < LOCK_STRIPES; i++) {
            stripes[i] = new Object();
        }
        return stripes;
    }

    public Path root() {
        return Paths.get(tmpPath).resolve(ROOT_DIR);
    }

    /** 当前登录用户的分片目录：tmp/upload-chunks/{userId}/{md5} */
    public Path chunkDir(String fileMd5) {
        if (fileMd5 == null || !HASH_PATTERN.matcher(fileMd5).matches()) {
            throw new GlobalException("文件MD5不合法");
        }
        Long userId = securityUtil.getUserInfo().getId();
        return root().resolve(String.valueOf(userId)).resolve(fileMd5.toLowerCase());
    }

    /** 同一目录的写入/合并互斥；key 用绝对路径，天然带 userId 维度 */
    public Object lock(Path chunkDir) {
        return locks[Math.floorMod(chunkDir.toString().hashCode(), LOCK_STRIPES)];
    }

    /** 标记该分片目录正在被使用，供清理任务判断"活跃" */
    public void touch(Path chunkDir) {
        try {
            Files.setLastModifiedTime(chunkDir, FileTime.fromMillis(System.currentTimeMillis()));
        } catch (IOException e) {
            log.debug("更新分片目录时间失败：{}", chunkDir, e);
        }
    }

    /**
     * 原子写入单个分片：先写 {index}.part，完成后再 move 成 {index}。
     * 已存在的有效分片直接跳过（幂等），并发/中断不会留下半截文件。
     */
    public void writeChunk(String fileMd5, int chunkIndex, MultipartFile chunk) throws IOException {
        if (chunkIndex < 0) {
            throw new GlobalException("分片索引不合法");
        }
        Path dir = chunkDir(fileMd5);
        Files.createDirectories(dir);
        Path target = dir.resolve(String.valueOf(chunkIndex));
        Path part = dir.resolve(chunkIndex + PART_SUFFIX);

        synchronized (lock(dir)) {
            // 幂等：已存在且长度与本次分片一致才跳过。
            // 旧分片若被截断/损坏，长度对不上，这里会重写，避免续传永远卡在"分片缺失"。
            if (Files.isRegularFile(target) && Files.size(target) > 0 && Files.size(target) == chunk.getSize()) {
                return;
            }
            try {
                try (InputStream is = chunk.getInputStream()) {
                    Files.copy(is, part, StandardCopyOption.REPLACE_EXISTING);
                }
                try {
                    Files.move(part, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException ignored) {
                    Files.move(part, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                // move 成功后 part 已不存在；异常时把半成品删掉，避免影响进度判定
                Files.deleteIfExists(part);
            }
            touch(dir);
        }
    }

    /**
     * 返回真正可续传的分片索引。
     * 除了文件名是纯数字外，还校验分片长度必须等于该索引应有的长度，
     * 这样被截断/损坏的旧分片不会被误认为已完成。
     */
    public List<Integer> uploadedIndices(String fileMd5, long fileSize, long chunkSize) {
        List<Integer> indices = new ArrayList<>();
        if (fileSize <= 0 || chunkSize <= 0) {
            return indices;
        }
        Path dir = chunkDir(fileMd5);
        if (!Files.isDirectory(dir)) {
            return indices;
        }
        long total = (fileSize + chunkSize - 1) / chunkSize;
        try (Stream<Path> paths = Files.list(dir)) {
            for (Path path : (Iterable<Path>) paths::iterator) {
                String name = path.getFileName().toString();
                if (!INDEX_PATTERN.matcher(name).matches() || !Files.isRegularFile(path)) {
                    continue;
                }
                int index;
                try {
                    index = Integer.parseInt(name);
                } catch (NumberFormatException ignored) {
                    continue;
                }
                if (index < 0 || index >= total) {
                    continue;
                }
                if (Files.size(path) == expectedChunkSize(index, fileSize, chunkSize, total)) {
                    indices.add(index);
                }
            }
        } catch (IOException e) {
            log.warn("读取分片进度失败：{}", dir, e);
        }
        return indices;
    }

    public static long expectedChunkSize(int index, long fileSize, long chunkSize, long total) {
        return index == total - 1 ? fileSize - (total - 1) * chunkSize : chunkSize;
    }

    /**
     * 合并成固定文件名 merged.tmp。此处不删除分片：
     * 只有 MinIO 上传成功并落库之后才整体删除目录，失败时分片仍在，可以直接重试。
     */
    public Path merge(String fileMd5, int total, long fileSize, long chunkSize) throws IOException {
        Path dir = chunkDir(fileMd5);
        Path merged = dir.resolve(MERGED_FILE);
        synchronized (lock(dir)) {
            Files.deleteIfExists(merged);
            try (FileChannel out = FileChannel.open(merged, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                for (int i = 0; i < total; i++) {
                    Path chunk = dir.resolve(String.valueOf(i));
                    long expected = expectedChunkSize(i, fileSize, chunkSize, total);
                    if (!Files.isRegularFile(chunk) || Files.size(chunk) != expected) {
                        throw new GlobalException("分片缺失或损坏，索引：" + i);
                    }
                    try (FileChannel in = FileChannel.open(chunk, StandardOpenOption.READ)) {
                        long transferred = 0;
                        while (transferred < expected) {
                            long n = in.transferTo(transferred, expected - transferred, out);
                            if (n <= 0) {
                                break;
                            }
                            transferred += n;
                        }
                        if (transferred != expected) {
                            throw new GlobalException("分片读取不完整，索引：" + i);
                        }
                    }
                }
            }
            touch(dir);
            return merged;
        }
    }

    /** 合并失败时只删产物，保留分片供重试 */
    public void deleteMerged(String fileMd5) {
        Path merged = chunkDir(fileMd5).resolve(MERGED_FILE);
        try {
            Files.deleteIfExists(merged);
        } catch (IOException e) {
            log.warn("删除合并临时文件失败：{}", merged, e);
        }
    }

    /** 上传成功后整体删除分片目录 */
    public void deleteChunks(String fileMd5) {
        deleteRecursively(chunkDir(fileMd5));
    }

    public static void deleteRecursively(Path path) {
        if (path == null || !Files.exists(path)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("删除分片失败：{}", p, e);
                }
            });
        } catch (IOException e) {
            log.warn("遍历分片目录失败：{}", path, e);
        }
    }

    /**
     * TTL 兜底清理。浏览器关闭、断网、进程重启都不会有回调，只有服务端按
     * "目录内最后一次写入时间" 才能可靠回收孤儿分片。
     *
     * @return 被回收的目录数量
     */
    public int cleanupExpired(Duration ttl, boolean cleanupLegacy) {
        long cutoff = System.currentTimeMillis() - ttl.toMillis();
        int removed = 0;
        Path root = root();
        if (Files.isDirectory(root)) {
            try (Stream<Path> userDirs = Files.list(root)) {
                for (Path userDir : (Iterable<Path>) userDirs::iterator) {
                    if (!Files.isDirectory(userDir)) {
                        continue;
                    }
                    try (Stream<Path> hashDirs = Files.list(userDir)) {
                        for (Path hashDir : (Iterable<Path>) hashDirs::iterator) {
                            if (!Files.isDirectory(hashDir)) {
                                continue;
                            }
                            // 先清掉过期的孤立 .part，再判断整个目录是否该回收
                            deleteStaleParts(hashDir, cutoff);
                            if (newestActivity(hashDir) < cutoff) {
                                deleteRecursively(hashDir);
                                removed++;
                            }
                        }
                    } catch (IOException e) {
                        log.warn("扫描用户分片目录失败：{}", userDir, e);
                    }
                    deleteIfEmpty(userDir);
                }
            } catch (IOException e) {
                log.warn("扫描分片根目录失败：{}", root, e);
            }
        }
        if (cleanupLegacy) {
            removed += cleanupLegacyDirs(cutoff);
        }
        return removed;
    }

    private void deleteStaleParts(Path dir, long cutoff) {
        try (Stream<Path> paths = Files.list(dir)) {
            for (Path path : (Iterable<Path>) paths::iterator) {
                if (!path.getFileName().toString().endsWith(PART_SUFFIX)) {
                    continue;
                }
                if (lastModified(path) < cutoff) {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        log.warn("删除孤立 .part 失败：{}", path, e);
                    }
                }
            }
        } catch (IOException e) {
            log.warn("扫描 .part 文件失败：{}", dir, e);
        }
    }

    /**
     * 兼容旧版本：改造前分片直接落在 tmp/{md5}，这些目录同样可能成为孤儿。
     * 只处理"32 位 hex 且目录内含纯数字文件名"的目录，避免误删 tmp 下其它内容。
     */
    private int cleanupLegacyDirs(long cutoff) {
        Path base = Paths.get(tmpPath);
        if (!Files.isDirectory(base)) {
            return 0;
        }
        int removed = 0;
        try (Stream<Path> dirs = Files.list(base)) {
            for (Path dir : (Iterable<Path>) dirs::iterator) {
                if (!Files.isDirectory(dir)
                        || !HASH_PATTERN.matcher(dir.getFileName().toString()).matches()
                        || !looksLikeChunkDir(dir)) {
                    continue;
                }
                if (newestActivity(dir) < cutoff) {
                    deleteRecursively(dir);
                    removed++;
                }
            }
        } catch (IOException e) {
            log.warn("扫描旧版分片目录失败：{}", base, e);
        }
        return removed;
    }

    private boolean looksLikeChunkDir(Path dir) {
        try (Stream<Path> paths = Files.list(dir)) {
            return paths.anyMatch(p -> INDEX_PATTERN.matcher(p.getFileName().toString()).matches());
        } catch (IOException e) {
            return false;
        }
    }

    private long newestActivity(Path dir) {
        long newest = lastModified(dir);
        try (Stream<Path> paths = Files.list(dir)) {
            for (Path path : (Iterable<Path>) paths::iterator) {
                newest = Math.max(newest, lastModified(path));
            }
        } catch (IOException e) {
            log.warn("读取分片目录内容失败：{}", dir, e);
        }
        return newest;
    }

    private long lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            // 读不到时间时按"刚刚活跃"处理，宁可晚删也不能误删正在上传的分片
            return System.currentTimeMillis();
        }
    }

    private void deleteIfEmpty(Path dir) {
        try (Stream<Path> paths = Files.list(dir)) {
            if (paths.findAny().isEmpty()) {
                Files.deleteIfExists(dir);
            }
        } catch (IOException e) {
            log.debug("清理空用户目录失败：{}", dir, e);
        }
    }
}
