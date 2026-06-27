package com.urlshortener.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UrlUtilsTest {

    @Test
    void normalizesValidUrls() {
        assertEquals("https://example.com/path", UrlUtils.normalize("https://example.com/path"));
        assertEquals("https:///path-without-host", UrlUtils.normalize("https:///path-without-host"));
    }

    @Test
    void rejectsInvalidUrls() {
        assertThrows(IllegalArgumentException.class, () -> UrlUtils.normalize("not-a-url"));
        assertThrows(IllegalArgumentException.class, () -> UrlUtils.normalize("ftp://example.com"));
        assertThrows(IllegalArgumentException.class, () -> UrlUtils.normalize("   "));
    }

    @Test
    void validatesTtl() {
        assertThrows(IllegalArgumentException.class, () -> UrlUtils.validateTtl(0L));
    }

    @Test
    void validatesCustomAlias() {
        assertDoesNotThrow(() -> UrlUtils.validateCustomAlias("my-link"));
        assertThrows(IllegalArgumentException.class, () -> UrlUtils.validateCustomAlias("ab"));
        assertThrows(IllegalArgumentException.class, () -> UrlUtils.validateCustomAlias("bad alias"));
        assertThrows(IllegalArgumentException.class, () -> UrlUtils.validateCustomAlias("api"));
    }

    @Test
    void generatesAliasWithExpectedLength() {
        String alias = UrlUtils.generateAlias("https://example.com/path");
        assertEquals(8, alias.length());
    }

    @Test
    void generatesDifferentAliasesForDifferentUrls() {
        String first = UrlUtils.generateAlias("https://example.com/a");
        String second = UrlUtils.generateAlias("https://example.com/b");
        assertEquals(8, first.length());
        assertEquals(8, second.length());
    }
}
