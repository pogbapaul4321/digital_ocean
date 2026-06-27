package com.urlshortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.urlshortener.dto.CreateUrlRequest;
import com.urlshortener.exception.AliasConflictException;
import com.urlshortener.exception.UrlExpiredException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:service-test;DB_CLOSE_DELAY=-1",
        "app.base-url=http://localhost:8080"
})
class UrlServiceTest {

    @Autowired
    private UrlService urlService;

    @Test
    void createsResolvesAndTracksAccessCount() {
        var created = urlService.create(new CreateUrlRequest("https://example.com/docs", "docs-link", null));
        assertEquals("docs-link", created.alias());

        String redirectTarget = urlService.resolveRedirect("docs-link");
        assertEquals("https://example.com/docs", redirectTarget);

        var metadata = urlService.getMetadata("docs-link");
        assertEquals(1L, metadata.accessCount());
    }

    @Test
    void rejectsDuplicateCustomAlias() {
        urlService.create(new CreateUrlRequest("https://example.com/a", "shared", null));
        assertThrows(
                AliasConflictException.class,
                () -> urlService.create(new CreateUrlRequest("https://example.com/b", "shared", null)));
    }

    @Test
    void handlesConcurrentCustomAliasCreation() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            List<Callable<Object>> tasks = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                tasks.add(() -> {
                    try {
                        urlService.create(new CreateUrlRequest("https://example.com/race", "race-alias", null));
                        return "success";
                    } catch (AliasConflictException ex) {
                        return "conflict";
                    }
                });
            }

            List<Future<Object>> results = executor.invokeAll(tasks);
            long successes = results.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .filter("success"::equals)
                    .count();

            assertEquals(1L, successes);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void expiresLinksAfterTtl() throws InterruptedException {
        urlService.create(new CreateUrlRequest("https://example.com/expired", "expires-soon", 1L));
        Thread.sleep(1100L);
        assertThrows(UrlExpiredException.class, () -> urlService.resolveRedirect("expires-soon"));
    }
}
