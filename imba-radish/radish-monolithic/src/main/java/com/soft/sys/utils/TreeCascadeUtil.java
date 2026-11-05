package com.soft.sys.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @Author: cyx
 * 树形数据级联删除辅助
 * <p>
 * 统一收集"自身 + 全部子孙"节点 id，供部门、菜单这类 parent_id 自关联表做级联删除。
 * 使用 Set 去重，同时过滤已收集节点，避免脏数据形成环时死循环。
 * @DateTime: 2026/11/05
 **/

public final class TreeCascadeUtil {

    private TreeCascadeUtil() {
    }

    /**
     * 广度优先收集自身及所有子孙节点 id
     *
     * @param rootIds        根节点 id 集合
     * @param childrenLoader 按父 id 集合查询直接子 id 集合的查询方法
     * @return 自身 + 子孙 id，根节点在前
     */
    public static List<Long> collectSelfAndDescendantIds(Collection<Long> rootIds,
                                                         Function<List<Long>, List<Long>> childrenLoader) {
        List<Long> result = new ArrayList<>();
        if (rootIds == null || rootIds.isEmpty()) {
            return result;
        }

        Set<Long> visited = new LinkedHashSet<>(rootIds);
        result.addAll(visited);

        List<Long> current = new ArrayList<>(visited);
        while (!current.isEmpty()) {
            List<Long> next = new ArrayList<>();
            List<Long> children = childrenLoader.apply(current);
            if (children == null || children.isEmpty()) {
                break;
            }
            for (Long childId : children) {
                if (childId != null && visited.add(childId)) {
                    next.add(childId);
                }
            }
            result.addAll(next);
            current = next;
        }
        return result;
    }
}
