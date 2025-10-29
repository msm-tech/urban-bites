package com.msmtech.restaurantapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseTest implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTest.class);

    private final DataSource dataSource;

    public DatabaseTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            logger.info("Database connection successful!");
            logger.info("Database: {}", connection.getMetaData().getDatabaseProductName());
            logger.info("Version: {}", connection.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            logger.error("Database connection failed: {}", e.getMessage(), e);
        }
    }
}