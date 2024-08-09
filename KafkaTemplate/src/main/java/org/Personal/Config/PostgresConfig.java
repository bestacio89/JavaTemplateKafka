package org.Personal.Config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration("spring.datasource")
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.Personal.Persistence.Postgres",
        entityManagerFactoryRef = "postgresEntityManagerFactory",
        transactionManagerRef = "postgresTransactionManager"
)
public class PostgresConfig {

    private static final Logger logger = LoggerFactory.getLogger(PostgresConfig.class);

    @Bean(name = "dataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "businessDataSource")
    public HikariDataSource dataSource(@Qualifier("dataSourceProperties") DataSourceProperties dataSourceProperties) {
        try {
            HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
            logger.info("HikariDataSource created successfully with URL: {}", dataSource.getJdbcUrl());
            return dataSource;
        } catch (Exception e) {
            logger.error("Error creating HikariDataSource", e);
            throw new RuntimeException("Failed to create HikariDataSource", e);
        }
    }

    @Bean(name = "postgresEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("businessDataSource") DataSource dataSource) {
        try {
            LocalContainerEntityManagerFactoryBean entityManagerFactory = builder
                    .dataSource(dataSource)
                    .packages("org.Personal.Persistence.Postgres")
                    .persistenceUnit("postgres")
                    .build();
            logger.info("EntityManagerFactory created successfully for persistence unit 'postgres'");
            return entityManagerFactory;
        } catch (Exception e) {
            logger.error("Error creating EntityManagerFactory", e);
            throw new RuntimeException("Failed to create EntityManagerFactory", e);
        }
    }

    @Bean(name = "postgresTransactionManager")
    public JpaTransactionManager transactionManager(
            @Qualifier("postgresEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        try {
            JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory);
            logger.info("JpaTransactionManager created successfully");
            return transactionManager;
        } catch (Exception e) {
            logger.error("Error creating JpaTransactionManager", e);
            throw new RuntimeException("Failed to create JpaTransactionManager", e);
        }
    }
}
