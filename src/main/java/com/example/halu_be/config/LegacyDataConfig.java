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
    entityManagerFactoryRef = "legacyEntityManagerFactory",
    transactionManagerRef = "legacyTransactionManager"
)
public class LegacyDataConfig {

    @Bean(name = "legacyDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.legacy")
    public DataSource legacyDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "legacyEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean legacyEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("legacyDataSource") DataSource dataSource
    ) {
        return builder
            .dataSource(dataSource)
            .packages("com.example.halu_be.models.secondary")
            .persistenceUnit("legacy")
            .build();
    }

    @Bean(name = "legacyTransactionManager")
    public PlatformTransactionManager legacyTransactionManager(
        @Qualifier("legacyEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
