@ApplicationModule(
        allowedDependencies = {
                "base::exception",
                "base::utils",
                "base::service",
                "base::entity"
        }
)
package com.soft.module;

import org.springframework.modulith.ApplicationModule;