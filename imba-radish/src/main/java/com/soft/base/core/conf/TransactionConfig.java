package com.soft.base.core.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @Author: cyx
 * @Description: spring事务管理配置
 * @DateTime: 2025/2/15 14:09
 **/
@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    /**
     * 动态数据源事务管理器
     * dynamic-datasource 会自动创建名为 'dataSource' 的 Bean
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
