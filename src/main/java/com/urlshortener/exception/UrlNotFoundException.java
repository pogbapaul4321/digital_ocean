package com.urlshortener.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String alias) {
        super("alias not found: " + alias);
    }
}
