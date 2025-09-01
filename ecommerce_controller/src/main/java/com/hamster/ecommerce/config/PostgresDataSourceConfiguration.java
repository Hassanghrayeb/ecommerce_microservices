package com.hamster.ecommerce.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EntityScan({"com.hamster.ecommerce.model.entity"})
@EnableJdbcRepositories(value = {"com.hamster.ecommerce"}, transactionManagerRef = "postgresTransactionManager")
@EnableTransactionManagement
public class PostgresDataSourceConfiguration
{
    @Bean
    @ConfigurationProperties(prefix = "spring.postgres")
    public DataSource dataSource()
    {
        return DataSourceBuilder.create().driverClassName("org.postgresql.Driver").build();
    }

    @Bean(name = "postgresTransactionManager")
    public DataSourceTransactionManager jdbcTransactionManager()
    {
        return new DataSourceTransactionManager(dataSource());
    }
}
