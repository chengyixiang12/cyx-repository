// src/utils/rsa-util.ts
import forge from 'node-forge';

export class RSAUtil {
  private static readonly PUBLIC_KEY_HEADER = '-----BEGIN PUBLIC KEY-----';
  private static readonly PUBLIC_KEY_FOOTER = '-----END PUBLIC KEY-----';

  /**
   * 使用 RSA 公钥加密（优化版）
   * @param plaintext 要加密的明文
   * @param publicKey 原始公钥字符串（不含PEM头尾）
   * @param options 加密选项
   * @returns 加密后的Base64密文
   * @throws 当参数无效或加密失败时抛出错误
   */
  static encrypt(
    plaintext: string,
    publicKey: string,
    options: {
      encoding?: 'base64' | 'hex';
      algorithm?: 'RSA-OAEP' | 'RSAES-PKCS1-V1_5';
    } = {}
  ): string {
    // 参数校验
    if (!plaintext || typeof plaintext !== 'string') {
      throw new Error('Plaintext must be a non-empty string');
    }

    if (!publicKey || typeof publicKey !== 'string') {
      throw new Error('Public key must be a non-empty string');
    }

    // 构造完整PEM格式公钥
    const pemKey = this.formatPublicKey(publicKey);

    try {
      // 加载公钥
      const rsa = forge.pki.publicKeyFromPem(pemKey);
      
      // 加密配置
      const {
        encoding = 'base64',
        algorithm = 'RSA-OAEP' // 默认使用更安全的OAEP填充
      } = options;

      // 执行加密
      const encrypted = rsa.encrypt(
        forge.util.encodeUtf8(plaintext), // 明确编码为UTF-8
        algorithm
      );

      // 返回指定编码
      return encoding === 'base64' 
        ? forge.util.encode64(encrypted)
        : forge.util.bytesToHex(encrypted);

    } catch (error) {
      throw new Error(`RSA encryption failed: ${error instanceof Error ? error.message : String(error)}`);
    }
  }

  /**
   * 格式化公钥为PEM格式
   * @param rawKey 原始公钥字符串
   * @returns 标准PEM格式公钥
   */
  private static formatPublicKey(rawKey: string): string {
    // 移除可能已存在的头尾
    const cleanKey = rawKey
      .replace(this.PUBLIC_KEY_HEADER, '')
      .replace(this.PUBLIC_KEY_FOOTER, '')
      .trim();

    return `${this.PUBLIC_KEY_HEADER}\n${cleanKey}\n${this.PUBLIC_KEY_FOOTER}`;
  }

  /**
   * 验证公钥格式是否有效
   * @param publicKey 原始公钥
   */
  static validatePublicKey(publicKey: string): boolean {
    try {
      const pem = this.formatPublicKey(publicKey);
      forge.pki.publicKeyFromPem(pem);
      return true;
    } catch {
      return false;
    }
  }
}