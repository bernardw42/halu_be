package com.example.halu_be.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.halu_be.repositories.secondary",
    entityManagerFactoryRef = "secondEntityManagerFactory",
    transactionManagerRef = "secondTransactionManager"
)
public class LegacyDataConfig {

    @Bean(name = "secondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "secondEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("secondDataSource") DataSource dataSource
    ) {
        return builder
            .dataSource(dataSource)
            .packages("com.example.halu_be.models.secondary")
            .persistenceUnit("second")
            .build();
    }

    @Bean(name = "secondTransactionManager")
    public PlatformTransactionManager secondTransactionManager(
        @Qualifier("secondEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
