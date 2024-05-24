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

@MapperScan(value = "com.harmoni.pos.menu.mapper")
@Configuration
public class DbMenuSQLConfig {
    public final static String BATCH_SKU_MAPPER = "skuMapper";

    @Primary
    @Bean(name = "menuSQLDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.menu.mysql")
    public DataSource menuSQLDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

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

    @Primary
    @Bean(name = "menuSQLTransactionManager")
    public DataSourceTransactionManager PrimaryTransactionManager (
            @Qualifier("menuSQLDataSource") DataSource menuSqlDatasource) {
        return new DataSourceTransactionManager(menuSqlDatasource);
    }

}
