package com.urlshortener.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public final class AliasGenerator {

    private static final String ALPHABET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_LENGTH = 8;

    private static final Set<String> RESERVED = Set.of("api", "health", "docs", "static");

    private AliasGenerator() {
    }

    public static String generate(String longUrl) {
        String input = longUrl + ":" + System.nanoTime();
        byte[] hash = sha256(input);

        StringBuilder builder = new StringBuilder(DEFAULT_LENGTH);
        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            int value = hash[i] & 0xFF;
            builder.append(ALPHABET.charAt(value % ALPHABET.length()));
        }
        return builder.toString();
    }

    public static void validateCustomAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("alias cannot be empty");
        }
        if (alias.length() < 3 || alias.length() > 64) {
            throw new IllegalArgumentException("alias must be between 3 and 64 characters");
        }
        if (!alias.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException(
                    "alias may only contain letters, numbers, hyphens, and underscores");
        }
        if (RESERVED.contains(alias.toLowerCase())) {
            throw new IllegalArgumentException("alias \"" + alias + "\" is reserved");
        }
    }

    private static byte[] sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
