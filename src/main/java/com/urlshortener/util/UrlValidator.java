package com.urlshortener.util;

import java.net.URI;
import java.net.URISyntaxException;

public final class UrlValidator {

    private UrlValidator() {
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
}
