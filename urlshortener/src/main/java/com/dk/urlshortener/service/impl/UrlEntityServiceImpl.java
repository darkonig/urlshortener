package com.dk.urlshortener.service.impl;

import com.dk.urlshortener.exception.EntityDuplicityException;
import com.dk.urlshortener.exception.EntityNotFoundException;
import com.dk.urlshortener.model.URLEntity;
import com.dk.urlshortener.repository.UrlEntityRepository;
import com.dk.urlshortener.service.UrlEntityService;
import com.google.common.hash.Hashing;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlEntityServiceImpl implements UrlEntityService {

    private UrlEntityRepository repository;

    public UrlEntityServiceImpl(UrlEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public URLEntity findOne(String id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Could not find the short url %s.", id)));
    }

    @Override
    public URLEntity save(String url) {
        Optional<URLEntity> model = repository.findByOrigin(url);
        if (model.isPresent()) {
            throw new EntityDuplicityException("Url already saved.");
        }

        String hash = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();

        URLEntity entity = URLEntity.builder()
                .origin(url)
                .hash(hash)
                .created(LocalDateTime.now())
                .build();

        return repository.save(entity);
    }

}
