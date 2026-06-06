package com.caloriestracker.system.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseKeepAlive {

    private final DataSource dataSource;

    public DatabaseKeepAlive(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Scheduled(fixedDelay = 240000)
    public void keepAlive() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT 1");
        } catch (Exception ignored) {}
    }
}
