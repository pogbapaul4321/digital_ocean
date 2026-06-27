package com.urlshortener.repository;

import com.urlshortener.entity.UrlEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UrlRepository extends JpaRepository<UrlEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UrlEntity u where u.alias = :alias")
    Optional<UrlEntity> findByAliasForUpdate(@Param("alias") String alias);
}
