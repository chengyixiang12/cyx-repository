package com.soft.sys.controller;

import com.soft.common.resultapi.R;
import com.soft.sys.model.vo.RedisKeyListVO;
import com.soft.sys.model.vo.RedisKeyValueVO;
import com.soft.sys.model.vo.RedisOverviewVO;
import com.soft.sys.model.vo.RedisPrefixGroupVO;
import com.soft.sys.service.RedisMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author cyx
 * @description Redis 缓存监控（只读：总览 / 前缀分组 / key 列表 / 值详情，敏感值脱敏）
 * @date 2026-11-27
 */
@RestController
@RequestMapping(value = "/redis")
@Slf4j
@Validated
@Tag(name = "Redis缓存监控")
@RequiredArgsConstructor
public class SysRedisController {

    private final RedisMonitorService redisMonitorService;

    @GetMapping(value = "/overview")
    @Operation(summary = "Redis 实例总览（key 总数、内存、命中率）")
    @PreAuthorize(value = "@cps.hasPermission('sys_redis_view')")
    public R<RedisOverviewVO> overview() {
        try {
            return R.ok(redisMonitorService.getOverview());
        } catch (Exception e) {
            log.error("获取Redis总览失败", e);
            return R.fail();
        }
    }

    @GetMapping(value = "/groups")
    @Operation(summary = "按业务前缀分组统计 key 数量")
    @PreAuthorize(value = "@cps.hasPermission('sys_redis_view')")
    public R<List<RedisPrefixGroupVO>> groups() {
        try {
            return R.ok(redisMonitorService.getPrefixGroups());
        } catch (Exception e) {
            log.error("获取Redis前缀分组失败", e);
            return R.fail();
        }
    }

    @GetMapping(value = "/keys")
    @Operation(summary = "SCAN 游标分页查询 key 列表（支持前缀过滤 + 模糊搜索）")
    @PreAuthorize(value = "@cps.hasPermission('sys_redis_view')")
    public R<RedisKeyListVO> keys(
            @Parameter(description = "key 前缀，为空表示全库", in = ParameterIn.QUERY)
            @RequestParam(value = "prefix", required = false) String prefix,
            @Parameter(description = "key 包含的关键字", in = ParameterIn.QUERY)
            @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "页码", in = ParameterIn.QUERY)
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页条数", in = ParameterIn.QUERY)
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        try {
            return R.ok(redisMonitorService.getKeys(prefix, keyword, page, size));
        } catch (Exception e) {
            log.error("查询Redis key列表失败", e);
            return R.fail();
        }
    }

    @GetMapping(value = "/value")
    @Operation(summary = "查看单个 key 详情（敏感前缀自动脱敏，不返回值）")
    @Parameter(name = "key", description = "Redis key", required = true, in = ParameterIn.QUERY)
    @PreAuthorize(value = "@cps.hasPermission('sys_redis_view')")
    public R<RedisKeyValueVO> value(@RequestParam(value = "key") @NotBlank(message = "key不能为空") String key) {
        try {
            return R.ok(redisMonitorService.getKeyValue(key));
        } catch (Exception e) {
            log.error("查看Redis key详情失败", e);
            return R.fail();
        }
    }
}
