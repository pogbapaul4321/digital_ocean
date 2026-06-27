package com.urlshortener.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:integration-test;DB_CLOSE_DELAY=-1",
        "app.base-url=http://example.test"
})
class UrlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createRedirectAndReturnMetadata() throws Exception {
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"url":"https://digitalocean.com","alias":"do-link"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alias").value("do-link"))
                .andExpect(jsonPath("$.short_url").value("http://example.test/do-link"));

        mockMvc.perform(get("/do-link"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://digitalocean.com"));

        mockMvc.perform(get("/api/urls/do-link"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_count").value(1));
    }
}
