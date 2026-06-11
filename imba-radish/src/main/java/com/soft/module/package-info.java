@ApplicationModule(
        allowedDependencies = {
                "sys::exception",
                "sys::utils",
                "sys::service",
                "sys::entity",
                "sys::websocket"
        }
)
package com.soft.module;

import org.springframework.modulith.ApplicationModule;