package com.urlshortener.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AliasGeneratorTest {

    @Test
    void validatesCustomAlias() {
        assertDoesNotThrow(() -> AliasGenerator.validateCustomAlias("my-link"));
        assertThrows(IllegalArgumentException.class, () -> AliasGenerator.validateCustomAlias("ab"));
        assertThrows(IllegalArgumentException.class, () -> AliasGenerator.validateCustomAlias("bad alias"));
        assertThrows(IllegalArgumentException.class, () -> AliasGenerator.validateCustomAlias("api"));
    }

    @Test
    void generatesAliasWithExpectedLength() {
        String alias = AliasGenerator.generate("https://example.com/path");
        assertEquals(8, alias.length());
    }

    @Test
    void generatesDeterministicLengthFromHashAndModulo() {
        String first = AliasGenerator.generate("https://example.com/a");
        String second = AliasGenerator.generate("https://example.com/b");
        assertEquals(8, first.length());
        assertEquals(8, second.length());
    }
}
