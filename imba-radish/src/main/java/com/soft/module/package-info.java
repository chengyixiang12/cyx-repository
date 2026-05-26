@ApplicationModule(
        allowedDependencies = {
                "sys::exception",
                "sys::utils",
                "sys::service",
                "sys::entity"
        }
)
package com.soft.module;

import org.springframework.modulith.ApplicationModule;