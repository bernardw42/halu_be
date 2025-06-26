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
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.halu_be.repositories", // Only main repositories, NOT .secondary
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.example\\.halu_be\\.repositories\\.secondary\\..*"
    ),
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
public class MainDataConfig {

    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        // Explicitly set properties to ensure DataSourceBuilder picks them up
        return DataSourceBuilder.create()
            .url("jdbc:mysql://localhost:3306/spring_ecommerce?useSSL=false&serverTimezone=UTC")
            .username("root")
            .password("")
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .build();
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("dataSource") DataSource dataSource
    ) {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        jpaProperties.put("hibernate.show_sql", "true");
        // add more if needed

        return builder
            .dataSource(dataSource)
            .packages("com.example.halu_be.models") // <-- Make sure this is correct
            .persistenceUnit("primary")
            .properties(jpaProperties)
            .build();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
        @Qualifier("entityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(
            new HibernateJpaVendorAdapter(),
            new HashMap<>(),
            null
        );
    }
}