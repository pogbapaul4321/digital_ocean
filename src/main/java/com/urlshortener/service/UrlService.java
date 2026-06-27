package com.urlshortener.service;

import com.urlshortener.cache.UrlCache;
import com.urlshortener.dto.CreateUrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.entity.UrlEntity;
import com.urlshortener.exception.AliasConflictException;
import com.urlshortener.exception.UrlExpiredException;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.util.AliasGenerator;
import com.urlshortener.util.UrlValidator;
import java.time.Instant;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {

    private static final int MAX_GENERATION_ATTEMPTS = 5;

    private final UrlRepository repository;
    private final UrlCache cache;
    private final String baseUrl;

    public UrlService(UrlRepository repository, UrlCache cache, String baseUrl) {
        this.repository = repository;
        this.cache = cache;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public UrlResponse create(CreateUrlRequest request) {
        String longUrl = UrlValidator.normalize(request.url());
        UrlValidator.validateTtl(request.ttlSeconds());

        Instant expiresAt = request.ttlSeconds() == null
                ? null
                : Instant.now().plusSeconds(request.ttlSeconds());

        String alias = request.alias();
        if (alias != null && !alias.isBlank()) {
            AliasGenerator.validateCustomAlias(alias.trim());
            return saveNewUrl(alias.trim(), longUrl, expiresAt);
        }

        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            try {
                return saveNewUrl(AliasGenerator.generate(longUrl), longUrl, expiresAt);
            } catch (AliasConflictException ignored) {
                // Retry with a newly generated alias.
            }
        }

        throw new IllegalStateException("failed to generate unique alias");
    }

    @Transactional(readOnly = true)
    public UrlResponse getMetadata(String alias) {
        UrlEntity entity = findActiveEntity(alias);
        return toResponse(entity);
    }

    @Transactional
    public String resolveRedirect(String alias) {
        UrlCache.CacheEntry cached = cache.get(alias);
        if (cached != null) {
            incrementAccessCount(alias);
            return cached.longUrl();
        }

        UrlEntity entity = findActiveEntity(alias);
        cache.put(alias, entity.getLongUrl(), entity.getExpiresAt());
        incrementAccessCount(alias);
        return entity.getLongUrl();
    }

    private UrlEntity findActiveEntity(String alias) {
        UrlEntity entity = repository.findById(alias)
                .orElseThrow(() -> new UrlNotFoundException(alias));

        if (entity.isExpired()) {
            cache.invalidate(alias);
            throw new UrlExpiredException(alias);
        }

        return entity;
    }

    private UrlResponse saveNewUrl(String alias, String longUrl, Instant expiresAt) {
        if (repository.existsById(alias)) {
            throw new AliasConflictException(alias);
        }

        UrlEntity entity = new UrlEntity(alias, longUrl, Instant.now(), expiresAt);
        try {
            repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new AliasConflictException(alias);
        }

        return toResponse(entity);
    }

    private void incrementAccessCount(String alias) {
        Optional<UrlEntity> entity = repository.findByAliasForUpdate(alias);
        if (entity.isEmpty()) {
            throw new UrlNotFoundException(alias);
        }

        UrlEntity url = entity.get();
        if (url.isExpired()) {
            cache.invalidate(alias);
            throw new UrlExpiredException(alias);
        }

        url.incrementAccessCount();
        repository.save(url);
    }

    private UrlResponse toResponse(UrlEntity entity) {
        return new UrlResponse(
                entity.getAlias(),
                baseUrl + "/" + entity.getAlias(),
                entity.getLongUrl(),
                entity.getCreatedAt(),
                entity.getAccessCount(),
                entity.getExpiresAt());
    }
}
