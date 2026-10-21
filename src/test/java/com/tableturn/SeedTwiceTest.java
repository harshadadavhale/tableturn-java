
package com.tableturn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.context.WebServerApplicationContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeedTwiceTest {

    @Test
    void startsTwiceAndReadsFloorAfterSecondStart() throws Exception {
        String databaseUrl =
                "jdbc:h2:file:./target/tableturn-idempotency-test;DB_CLOSE_ON_EXIT=FALSE";

        // First startup: initialize and seed the database.
        try (ConfigurableApplicationContext first = startApp(databaseUrl)) {
            assertEquals(22, first.getBean(
                    com.tableturn.repository.TableRepository.class)
                    .findAll().size());
        }

        // Second startup: use the EXACT same database URL.
        try (ConfigurableApplicationContext second = startApp(databaseUrl)) {
            int port = ((WebServerApplicationContext) second)
                    .getWebServer().getPort();

            // Read the floor over HTTP after the second startup.
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://127.0.0.1:" + port +
                            "/branches/Baner/tables"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    HttpClient.newHttpClient().send(
                            request,
                            HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            JsonNode floor = new ObjectMapper().readTree(response.body());

            assertEquals(22, floor.get("tables").size());
            assertEquals(22, floor.get("free").asInt());
        }
    }

    private ConfigurableApplicationContext startApp(String databaseUrl) {
        return new SpringApplicationBuilder(TableTurnApplication.class)
                .properties(
                        "spring.datasource.url=" + databaseUrl,
                        "server.port=0"
                )
                .run();
    }
}
