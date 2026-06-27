package com.urlshortener.cache;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UrlCache {

    private static final int MAX_SIZE = 1024;

    private final Map<String, CacheEntry> entries = new LinkedHashMap<>(MAX_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
            return size() > MAX_SIZE;
        }
    };

    public synchronized CacheEntry get(String alias) {
        CacheEntry entry = entries.get(alias);
        if (entry == null) {
            return null;
        }
        if (entry.expiresAt() != null && Instant.now().isAfter(entry.expiresAt())) {
            entries.remove(alias);
            return null;
        }
        return entry;
    }

    public synchronized void put(String alias, String longUrl, Instant expiresAt) {
        entries.put(alias, new CacheEntry(longUrl, expiresAt));
    }

    public synchronized void invalidate(String alias) {
        entries.remove(alias);
    }

    public record CacheEntry(String longUrl, Instant expiresAt) {
    }
}
