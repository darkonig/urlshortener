package com.dk.urlshortener.urlshortenerstatus.repository;

import com.dk.urlshortener.urlshortenerstatus.model.URLEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface URLEntityRepository extends CrudRepository<URLEntity, String> {

    Optional<URLEntity> findByOrigin(String origin);

}
