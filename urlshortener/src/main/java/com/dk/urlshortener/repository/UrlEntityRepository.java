package com.dk.urlshortener.repository;

import com.dk.urlshortener.model.URLEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlEntityRepository extends CrudRepository<URLEntity, String> {

    Optional<URLEntity> findByOrigin(String url);
    
}
