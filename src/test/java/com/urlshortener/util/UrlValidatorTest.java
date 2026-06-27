package com.urlshortener.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UrlValidatorTest {

    @Test
    void normalizesValidUrls() {
        assertEquals("https://example.com/path", UrlValidator.normalize("https://example.com/path"));
        assertEquals("https:///path-without-host", UrlValidator.normalize("https:///path-without-host"));
    }

    @Test
    void rejectsInvalidUrls() {
        assertThrows(IllegalArgumentException.class, () -> UrlValidator.normalize("not-a-url"));
        assertThrows(IllegalArgumentException.class, () -> UrlValidator.normalize("ftp://example.com"));
        assertThrows(IllegalArgumentException.class, () -> UrlValidator.normalize("   "));
    }

    @Test
    void validatesTtl() {
        assertThrows(IllegalArgumentException.class, () -> UrlValidator.validateTtl(0L));
    }
}
