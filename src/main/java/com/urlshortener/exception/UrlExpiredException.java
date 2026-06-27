package com.urlshortener.exception;

public class UrlExpiredException extends RuntimeException {

    public UrlExpiredException(String alias) {
        super("short url has expired: " + alias);
    }
}
