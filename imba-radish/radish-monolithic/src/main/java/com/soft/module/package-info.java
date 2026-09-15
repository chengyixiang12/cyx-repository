@ApplicationModule(
        // 注：sys::exception 与 sys::resultapi 已随 radish-common 抽出，不再作为 sys 的命名接口；
        // 本模块对它们的依赖现在指向公共库 com.soft.common。
        allowedDependencies = {
                "sys::utils",
                "sys::service",
                "sys::entity",
                "sys::websocket"
        }
)
package com.soft.module;

import org.springframework.modulith.ApplicationModule;
