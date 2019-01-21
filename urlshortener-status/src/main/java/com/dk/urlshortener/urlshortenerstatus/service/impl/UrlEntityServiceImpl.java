package com.dk.urlshortener.urlshortenerstatus.service.impl;

import com.dk.urlshortener.urlshortenerstatus.exception.EntityDuplicityException;
import com.dk.urlshortener.urlshortenerstatus.exception.EntityNotFoundException;
import com.dk.urlshortener.urlshortenerstatus.repository.URLEntityRepository;
import com.dk.urlshortener.urlshortenerstatus.repository.URLEventRepository;
import com.dk.urlshortener.urlshortenerstatus.model.URLEntity;
import com.dk.urlshortener.urlshortenerstatus.model.URLEvent;
import com.dk.urlshortener.urlshortenerstatus.service.UrlEntityService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UrlEntityServiceImpl implements UrlEntityService {

    private URLEntityRepository repository;
    private URLEventRepository eventRepository;

    public UrlEntityServiceImpl(URLEntityRepository repository, URLEventRepository eventRepository) {
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    @Override
    public URLEntity save(URLEntity entity) {
        Optional<URLEntity> e = repository.findByOrigin(entity.getOrigin());
        if (e.isPresent()) {
            throw new EntityDuplicityException("Entity already registered.");
        }

        return repository.save(entity);
    }

    @Override
    public URLEvent saveEvent(URLEvent event) {
        return eventRepository.save(event);
    }

    @Override
    public URLEntity findOne(String hash) {
        return repository.findById(hash).orElseThrow(() -> new EntityNotFoundException("Url not found."));
    }

    @Override
    public List<URLEvent> findEvents(URLEntity entity, int page, int size) {
        return eventRepository.findAllByEntity(entity, PageRequest.of(page, size, Sort.by(Sort.Order.desc("date"))));
    }

    @Override
    public long countTotalEvents(URLEntity e) {
        return eventRepository.countByEntity(e);
    }
}
