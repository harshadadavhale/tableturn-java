package com.tableturn.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
public class FloorSeeder {

    @Bean
    SmartInitializingSingleton seedTables(JdbcClient jdbc) {
        return () -> {
            Long count = jdbc.sql("SELECT COUNT(*) FROM tables")
                    .query(Long.class)
                    .single();

            // Do not insert duplicates when the application restarts.
            if (count != 0) {
                return;
            }

            try (BufferedReader reader = openSeedFile()) {
                String line;
                boolean firstRow = true;
                int inserted = 0;

                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    String[] columns = line.split(",", -1);

                    if (firstRow) {
                        firstRow = false;

                        // Skip the header row if present.
                        if (columns[0].trim().equalsIgnoreCase("no")) {
                            continue;
                        }
                    }

                    if (columns.length < 3) {
                        throw new IllegalStateException(
                                "Invalid CSV row: " + line);
                    }

                    String no = columns[0].trim();
                    int seats = Integer.parseInt(columns[1].trim());
                    String section = columns[2].trim();

                    jdbc.sql("""
                            INSERT INTO tables
                                (no, seats, section, current_seating_id)
                            VALUES (:no, :seats, :section, NULL)
                            """)
                        .param("no", no)
                        .param("seats", seats)
                        .param("section", section)
                        .update();

                    inserted++;
                }

                if (inserted != 22) {
                    throw new IllegalStateException(
                            "Expected 22 tables in seed/tables.csv, but loaded "
                                    + inserted);
                }

            } catch (IOException e) {
                throw new IllegalStateException(
                        "Could not read seed/tables.csv", e);
            }
        };
    }

    private BufferedReader openSeedFile() throws IOException {
        Path file = Path.of("seed", "tables.csv");

        InputStream stream;

        if (Files.exists(file)) {
            stream = Files.newInputStream(file);
        } else {
            stream = new ClassPathResource("seed/tables.csv")
                    .getInputStream();
        }

        return new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8));
    }
}