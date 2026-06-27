package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUrlRequest(
        @NotBlank(message = "url is required") String url,
        String alias,
        Long ttlSeconds) {
}
