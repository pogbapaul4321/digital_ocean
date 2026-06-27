package com.urlshortener.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public final class UrlUtils {

    private static final String ALPHABET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_LENGTH = 8;
    private static final Set<String> RESERVED = Set.of("api", "health", "docs", "static");

    private UrlUtils() {
    }

    public static String normalize(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new IllegalArgumentException("url is required");
        }

        String trimmed = rawUrl.trim();
        URI uri;
        try {
            uri = new URI(trimmed);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("invalid url: " + ex.getMessage());
        }

        if (uri.getScheme() == null) {
            throw new IllegalArgumentException("url must include a valid http or https scheme");
        }

        String scheme = uri.getScheme().toLowerCase();
        if (!scheme.equals("http") && !scheme.equals("https")) {
            throw new IllegalArgumentException("url must use http or https scheme");
        }

        return uri.toString();
    }

    public static void validateTtl(Long ttlSeconds) {
        if (ttlSeconds == null) {
            return;
        }
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("ttl_seconds must be greater than zero");
        }
    }

    public static String generateAlias(String longUrl) {
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
