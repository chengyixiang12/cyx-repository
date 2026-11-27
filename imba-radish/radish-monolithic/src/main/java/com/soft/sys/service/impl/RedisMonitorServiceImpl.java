package com.soft.sys.service.impl;

import com.soft.common.enums.RedisKeyPrefix;
import com.soft.sys.model.vo.RedisKeyListVO;
import com.soft.sys.model.vo.RedisKeyVO;
import com.soft.sys.model.vo.RedisKeyValueVO;
import com.soft.sys.model.vo.RedisOverviewVO;
import com.soft.sys.model.vo.RedisPrefixGroupVO;
import com.soft.sys.service.RedisMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author cyx
 * @description Redis 缓存监控实现：全程只读，SCAN 游标遍历（禁用 KEYS），敏感值脱敏
 * @date 2026-11-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMonitorServiceImpl implements RedisMonitorService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 单次 SCAN 扫描上限（防止 DB 异常膨胀时拖垮接口）
     */
    private static final long SCAN_LIMIT = 5000;

    /**
     * 前缀分组统计时的全库扫描上限
     */
    private static final long GROUP_SCAN_LIMIT = 100000;

    /**
     * 集合类型值的展示条数上限
     */
    private static final long VALUE_PREVIEW_LIMIT = 100;

    /*
     * 前缀含义与敏感标记统一由 RedisKeyPrefix 枚举承载，本类不再重复维护映射
     */

    @Override
    public RedisOverviewVO getOverview() {
        RedisOverviewVO vo = new RedisOverviewVO();
        // 分别拉取各 section，避免依赖 INFO 全量返回的扁平化行为差异
        Properties server = info("server");
        Properties memory = info("memory");
        Properties stats = info("stats");
        Properties clients = info("clients");

        vo.setDbSize(dbSize());
        vo.setRedisVersion(server.getProperty("redis_version"));
        vo.setUptimeDays(parseLong(server.getProperty("uptime_in_days")));
        vo.setUsedMemoryBytes(parseLong(memory.getProperty("used_memory")));
        vo.setUsedMemoryHuman(memory.getProperty("used_memory_human"));
        vo.setMaxMemoryHuman(memory.getProperty("maxmemory_human"));
        vo.setMemFragmentationRatio(parseDouble(memory.getProperty("mem_fragmentation_ratio")));
        vo.setConnectedClients(parseLong(clients.getProperty("connected_clients")));
        vo.setKeyspaceHits(parseLong(stats.getProperty("keyspace_hits")));
        vo.setKeyspaceMisses(parseLong(stats.getProperty("keyspace_misses")));

        long hits = vo.getKeyspaceHits() == null ? 0 : vo.getKeyspaceHits();
        long misses = vo.getKeyspaceMisses() == null ? 0 : vo.getKeyspaceMisses();
        if (hits + misses > 0) {
            vo.setHitRate(Math.round(hits * 10000.0 / (hits + misses)) / 100.0);
        }
        return vo;
    }

    @Override
    public List<RedisPrefixGroupVO> getPrefixGroups() {
        // 一次全库 SCAN（带上限），按前缀归桶，避免对每个前缀重复扫描
        Map<RedisKeyPrefix, Long> counts = new EnumMap<>(RedisKeyPrefix.class);
        long[] scanned = {0};
        long[] otherCount = {0};

        redisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.keyCommands()
                    .scan(ScanOptions.scanOptions().match("*").count(500).build())) {
                while (cursor.hasNext() && scanned[0] < GROUP_SCAN_LIMIT) {
                    String key = new String(cursor.next(), StandardCharsets.UTF_8);
                    scanned[0]++;
                    RedisKeyPrefix prefix = RedisKeyPrefix.matchOf(key);
                    if (prefix != null) {
                        counts.merge(prefix, 1L, Long::sum);
                    } else {
                        otherCount[0]++;
                    }
                }
            }
            return null;
        });

        // 枚举即分组来源：含义、敏感标记全部来自 RedisKeyPrefix，无 key 的前缀返回数量 0
        List<RedisPrefixGroupVO> groups = new ArrayList<>();
        for (RedisKeyPrefix item : RedisKeyPrefix.values()) {
            groups.add(new RedisPrefixGroupVO(item.getPrefix(), item.getDescription(),
                    counts.getOrDefault(item, 0L), item.isSensitive()));
        }
        if (otherCount[0] > 0) {
            groups.add(new RedisPrefixGroupVO("（其他）", "未匹配任何业务前缀", otherCount[0], false));
        }
        // 数量多的排前面，方便定位
        groups.sort(Comparator.comparing(RedisPrefixGroupVO::getKeyCount).reversed());
        return groups;
    }

    @Override
    public RedisKeyListVO getKeys(String prefix, String keyword, Integer page, Integer size) {
        int pageNum = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1 || size > 100) ? 20 : size;

        String match = (prefix == null || prefix.isBlank()) ? "*" : prefix + "*";
        // 多扫 1 条用于判断是否被截断
        long scanLimit = SCAN_LIMIT + 1;
        long[] scanned = {0};
        boolean[] truncated = {false};

        List<String> keys = redisTemplate.execute((RedisCallback<List<String>>) connection -> {
            List<String> result = new ArrayList<>();
            try (Cursor<byte[]> cursor = connection.keyCommands()
                    .scan(ScanOptions.scanOptions().match(match).count(500).build())) {
                while (cursor.hasNext()) {
                    if (scanned[0] >= scanLimit) {
                        truncated[0] = true;
                        break;
                    }
                    result.add(new String(cursor.next(), StandardCharsets.UTF_8));
                    scanned[0]++;
                }
            }
            return result;
        });

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            keys = keys.stream().filter(key -> key.contains(kw)).toList();
        }
        keys.sort(Comparator.naturalOrder());

        long total = keys.size();
        int from = (pageNum - 1) * pageSize;
        List<String> pageKeys = from >= total
                ? List.of()
                : keys.subList(from, (int) Math.min(from + (long) pageSize, total));

        List<RedisKeyVO> records = pageKeys.stream().map(this::buildKeyVO).toList();
        // truncated 只反映扫描截断；关键字过滤后 total < SCAN_LIMIT 时不提示
        boolean realTruncated = truncated[0];
        return new RedisKeyListVO(records, total, realTruncated, scanned[0]);
    }

    @Override
    public RedisKeyValueVO getKeyValue(String key) {
        RedisKeyValueVO vo = new RedisKeyValueVO();
        vo.setKey(key);
        vo.setMasked(false);
        vo.setParseFailed(false);
        vo.setTruncated(false);
        vo.setEntryCount(0L);
        vo.setValue(null);

        String maskReason = sensitiveMaskReason(key);
        if (maskReason != null) {
            // 敏感 key：不触碰值，只返回元信息
            vo.setMasked(true);
            vo.setMaskReason(maskReason);
            vo.setType(typeName(key));
            vo.setTtlSeconds(ttlSeconds(key));
            return vo;
        }

        // 防呆：key 不存在时直接返回空壳
        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.FALSE.equals(exists)) {
            vo.setType("none");
            vo.setTtlSeconds(-2L);
            return vo;
        }

        String type = typeName(key);
        vo.setType(type);
        vo.setTtlSeconds(ttlSeconds(key));
        readValue(key, type, vo);
        return vo;
    }

    // ==================== 内部方法 ====================

    private void readValue(String key, String type, RedisKeyValueVO vo) {
        try {
            switch (type) {
                case "string" -> {
                    Object value = redisTemplate.opsForValue().get(key);
                    vo.setValue(value);
                }
                case "hash" -> {
                    Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
                    vo.setEntryCount((long) entries.size());
                    vo.setValue(entries);
                }
                case "list" -> {
                    List<Object> range = redisTemplate.opsForList().range(key, 0, VALUE_PREVIEW_LIMIT - 1);
                    long size = redisTemplate.opsForList().size(key) == null ? 0 : redisTemplate.opsForList().size(key);
                    vo.setEntryCount(size);
                    vo.setTruncated(size > VALUE_PREVIEW_LIMIT);
                    vo.setValue(range);
                }
                case "set" -> {
                    Set<Object> members = redisTemplate.opsForSet().members(key);
                    vo.setEntryCount((long) members.size());
                    // Set 顺序不稳定，排序后展示
                    vo.setValue(new ArrayList<>(members));
                }
                case "zset" -> {
                    Set<Object> tuples = new LinkedHashSet<>();
                    redisTemplate.opsForZSet().rangeWithScores(key, 0, VALUE_PREVIEW_LIMIT - 1)
                            .forEach(tuple -> {
                                Map<String, Object> item = new LinkedHashMap<>();
                                item.put("value", tuple.getValue());
                                item.put("score", tuple.getScore());
                                tuples.add(item);
                            });
                    Long size = redisTemplate.opsForZSet().size(key);
                    vo.setEntryCount(size == null ? 0 : size);
                    vo.setTruncated(size != null && size > VALUE_PREVIEW_LIMIT);
                    vo.setValue(tuples);
                }
                default -> vo.setValue("（不支持的类型：" + type + "）");
            }
        } catch (SerializationException | DataAccessException e) {
            // 典型场景：cacheManager 的 radish:users:: 使用 JDK 序列化，无法按 JSON 解析
            log.warn("解析 Redis key 值失败: {}", key, e);
            vo.setParseFailed(true);
            vo.setValue(null);
            vo.setMaskReason("值反序列化失败（可能为 JDK 序列化的二进制缓存），仅展示元信息");
        }
    }

    private RedisKeyVO buildKeyVO(String key) {
        RedisKeyVO vo = new RedisKeyVO();
        vo.setKey(key);
        vo.setType(typeName(key));
        vo.setTtlSeconds(ttlSeconds(key));
        vo.setSensitive(sensitiveMaskReason(key) != null);
        return vo;
    }

    private String typeName(String key) {
        try {
            org.springframework.data.redis.connection.DataType dataType = redisTemplate.type(key);
            return dataType == null ? "none" : dataType.code();
        } catch (DataAccessException e) {
            return "none";
        }
    }

    private Long ttlSeconds(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (DataAccessException e) {
            return -2L;
        }
    }

    /**
     * 命中敏感前缀时返回脱敏原因（含枚举中文含义），未命中返回 null
     */
    private String sensitiveMaskReason(String key) {
        RedisKeyPrefix item = RedisKeyPrefix.matchOf(key);
        if (item != null && item.isSensitive()) {
            return "敏感数据（" + item.getDescription() + "），值已脱敏，不对外展示";
        }
        return null;
    }

    private Properties info(String section) {
        return redisTemplate.execute((RedisCallback<Properties>) connection ->
                connection.serverCommands().info(section));
    }

    private Long dbSize() {
        return redisTemplate.execute((RedisCallback<Long>) connection -> connection.serverCommands().dbSize());
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
