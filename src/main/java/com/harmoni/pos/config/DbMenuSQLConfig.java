package com.harmoni.pos.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Configuration class for Menu SQL database connection and MyBatis integration.
 */
@MapperScan(value = "com.harmoni.pos.menu.mapper")
@Configuration
public class DbMenuSQLConfig {
    public static final String BATCH_SKU_MAPPER = "skuMapper";

    /**
     * Creates and configures the primary DataSource bean for the menu database.
     *
     * @return the configured DataSource
     */
    @Primary
    @Bean(name = "menuSQLDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.menu.mysql")
    public DataSource menuSQLDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    /**
     * Creates and configures the primary SqlSessionFactory bean for MyBatis.
     *
     * @param menuSQLDataSource the DataSource for the menu database
     * @param applicationContext the Spring application context
     * @return the configured SqlSessionFactory
     * @throws Exception if an error occurs during creation
     */
    @Primary
    @Bean(name = "menuSqlSessionFactory")
    public SqlSessionFactory menuSqlSessionFactory(
            @Qualifier("menuSQLDataSource") DataSource menuSQLDataSource,
            ApplicationContext applicationContext
    ) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(menuSQLDataSource);
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * Creates and configures the primary DataSourceTransactionManager bean for the menu database.
     *
     * @param menuSqlDatasource the DataSource for the menu database
     * @return the configured DataSourceTransactionManager
     */
    @Primary
    @Bean(name = "menuSQLTransactionManager")
    public DataSourceTransactionManager primaryTransactionManager (
            @Qualifier("menuSQLDataSource") DataSource menuSqlDatasource) {
        return new DataSourceTransactionManager(menuSqlDatasource);
    }

}
