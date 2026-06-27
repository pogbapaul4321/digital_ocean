package com.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UrlResponse(
        String alias,
        String shortUrl,
        String longUrl,
        Instant createdAt,
        long accessCount,
        Instant expiresAt) {
}
