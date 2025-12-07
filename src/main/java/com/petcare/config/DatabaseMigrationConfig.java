package com.petcare.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseMigrationConfig {

    @Bean
    CommandLineRunner runFlywayMigration(DataSource dataSource) {
        return args -> {
            System.out.println("========================================");
            System.out.println("STARTING FLYWAY MIGRATION");
            System.out.println("========================================");
            
            try {
                Flyway flyway = Flyway.configure()
                        .dataSource(dataSource)
                        .locations("classpath:db/migration")
                        .baselineOnMigrate(true)
                        .load();
                
                System.out.println("Flyway configuration loaded successfully");
                System.out.println("Running repair...");
                flyway.repair();
                
                System.out.println("Running migrations...");
                int migrationsApplied = flyway.migrate().migrationsExecuted;
                
                System.out.println("========================================");
                System.out.println("FLYWAY MIGRATION COMPLETED");
                System.out.println("Migrations applied: " + migrationsApplied);
                System.out.println("========================================");
            } catch (Exception e) {
                System.err.println("========================================");
                System.err.println("FLYWAY MIGRATION FAILED!");
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                System.err.println("========================================");
                throw e;
            }
        };
    }
}
