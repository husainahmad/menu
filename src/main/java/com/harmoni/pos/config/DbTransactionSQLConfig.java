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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Configuration class for Transaction SQL database connection and MyBatis integration.
 */
@MapperScan(value = "com.harmoni.pos.transaction.mapper")
@Configuration
public class DbTransactionSQLConfig {

    /**
     * Creates and configures the DataSource bean for the transaction database.
     *
     * @return the configured DataSource
     */
    @Bean(name = "transactionSQLDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.transaction.mysql")
    public DataSource transactionSQLDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    /**
     * Creates and configures the SqlSessionFactory bean for MyBatis.
     *
     * @param transactionSQLDataSource the DataSource for the transaction database
     * @param context the Spring application context
     * @return the configured SqlSessionFactory
     * @throws Exception if an error occurs during creation
     */
    @Bean(name = "transactionSqlSessionFactory")
    public SqlSessionFactory transactionSqlSessionFactory(
            @Qualifier("transactionSQLDataSource") DataSource transactionSQLDataSource,
            ApplicationContext context
    ) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(transactionSQLDataSource);
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * Creates and configures the DataSourceTransactionManager bean for the transaction database.
     *
     * @param menuSqlDatasource the DataSource for the transaction database
     * @return the configured DataSourceTransactionManager
     */
    @Bean(name = "transactionSQLTransactionManager")
    public DataSourceTransactionManager primaryTransactionManager (
            @Qualifier("transactionSQLDataSource") DataSource menuSqlDatasource) {
        return new DataSourceTransactionManager(menuSqlDatasource);
    }
}
