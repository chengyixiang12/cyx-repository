package com.soft.sys.service;

import com.soft.sys.model.vo.RedisKeyListVO;
import com.soft.sys.model.vo.RedisKeyValueVO;
import com.soft.sys.model.vo.RedisOverviewVO;
import com.soft.sys.model.vo.RedisPrefixGroupVO;

import java.util.List;

/**
 * @author cyx
 * @description Redis 缓存监控（只读）
 * @date 2026-11-27
 */
public interface RedisMonitorService {

    /**
     * Redis 实例总览：key 总数、内存、命中率等
     */
    RedisOverviewVO getOverview();

    /**
     * 按业务前缀分组统计 key 数量
     */
    List<RedisPrefixGroupVO> getPrefixGroups();

    /**
     * SCAN 游标扫描 key 列表（非阻塞），支持前缀过滤 + 模糊搜索 + 内存分页
     *
     * @param prefix  key 前缀，为空表示全库
     * @param keyword key 包含的关键字（在前缀过滤结果上二次过滤）
     * @param page    页码（1 起）
     * @param size    每页条数
     */
    RedisKeyListVO getKeys(String prefix, String keyword, Integer page, Integer size);

    /**
     * 查看单个 key 的详情；敏感前缀（token/验证码/密钥等）一律脱敏，不返回值
     */
    RedisKeyValueVO getKeyValue(String key);
}
