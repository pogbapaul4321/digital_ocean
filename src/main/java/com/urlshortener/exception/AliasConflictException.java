package com.urlshortener.exception;

public class AliasConflictException extends RuntimeException {

    public AliasConflictException(String alias) {
        super("alias already exists: " + alias);
    }
}
