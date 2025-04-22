package com.soft.base.model.ctf;

import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/22 10:34
 **/
public interface MenuTree<T extends MenuTree<T>> {
    Long getId();

    Long getParentId();

    List<T> getChildren();
}
