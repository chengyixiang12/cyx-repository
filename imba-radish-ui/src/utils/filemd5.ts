import SparkMD5 from "spark-md5";
import { showMessage } from "./message";

export const chunkSize = 5 * 1024 * 1024;
/**
 * 计算文件MD5（用于分片唯一标识）
 */
export const calculateFileMd5 = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const spark = new SparkMD5.ArrayBuffer();
    const fileReader = new FileReader();
    const chunks = Math.ceil(file.size / chunkSize);
    let currentChunk = 0;

    fileReader.onload = (e) => {
      try {
        spark.append(e.target?.result as ArrayBuffer);
        currentChunk++;
        // 读取完所有块后计算MD5
        if (currentChunk >= chunks) {
          resolve(spark.end());
        } else {
          loadNextChunk();
        }
      } catch (err) {
        reject(err);
      }
    };

    fileReader.onerror = (err) => {
      reject(err);
      showMessage('文件上传失败', 'error');
    };

    // 读取下一块
    const loadNextChunk = () => {
      const start = currentChunk * chunkSize;
      const end = Math.min(start + chunkSize, file.size);
      fileReader.readAsArrayBuffer(file.slice(start, end));
    };

    // 开始读取第一块
    loadNextChunk();
  });
};