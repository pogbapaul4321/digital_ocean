package com.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "urls")
public class UrlEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String alias;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "access_count", nullable = false)
    private long accessCount;

    @Column(name = "expires_at")
    private Instant expiresAt;

    protected UrlEntity() {
    }

    public UrlEntity(String alias, String longUrl, Instant createdAt, Instant expiresAt) {
        this.alias = alias;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
        this.accessCount = 0L;
        this.expiresAt = expiresAt;
    }

    public String getAlias() {
        return alias;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getAccessCount() {
        return accessCount;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void incrementAccessCount() {
        this.accessCount++;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
}
