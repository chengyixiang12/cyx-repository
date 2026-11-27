/**
 * Redis 缓存监控相关类型
 */

export interface RedisOverviewVo {
    dbSize?: number;
    redisVersion?: string;
    usedMemoryHuman?: string;
    usedMemoryBytes?: number;
    maxMemoryHuman?: string;
    memFragmentationRatio?: number;
    connectedClients?: number;
    keyspaceHits?: number;
    keyspaceMisses?: number;
    hitRate?: number;
    uptimeDays?: number;
}

export interface RedisPrefixGroupVo {
    prefix: string;
    businessName: string;
    keyCount: number;
    sensitive: boolean;
}

export interface RedisKeyVo {
    key: string;
    type: string;
    ttlSeconds: number;
    sensitive: boolean;
}

export interface RedisKeyListVo {
    records: RedisKeyVo[];
    total: number;
    truncated: boolean;
    scanned: number;
}

export interface RedisKeyValueVo {
    key: string;
    type: string;
    ttlSeconds: number;
    masked: boolean;
    maskReason?: string;
    value?: any;
    entryCount: number;
    truncated: boolean;
    parseFailed: boolean;
}
