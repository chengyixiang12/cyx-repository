import { get } from '@/utils/http'
import type { RedisKeyListVo, RedisKeyValueVo, RedisOverviewVo, RedisPrefixGroupVo } from '@/types/redis'

/**
 * Redis 实例总览
 */
export async function getRedisOverviewApi(): Promise<RedisOverviewVo> {
    const res = await get('/redis/overview', { flag: true });
    return res.data;
}

/**
 * 按业务前缀分组统计 key 数量
 */
export async function getRedisGroupsApi(): Promise<RedisPrefixGroupVo[]> {
    const res = await get('/redis/groups', { flag: true });
    return res.data;
}

/**
 * SCAN 分页查询 key 列表
 */
export async function getRedisKeysApi(params: {
    prefix?: string;
    keyword?: string;
    page?: number;
    size?: number;
}): Promise<RedisKeyListVo> {
    const res = await get('/redis/keys', { flag: true, params });
    return res.data;
}

/**
 * 查看单个 key 详情（敏感值脱敏）
 */
export async function getRedisValueApi(key: string): Promise<RedisKeyValueVo> {
    const res = await get('/redis/value', { flag: true, params: { key } });
    return res.data;
}
