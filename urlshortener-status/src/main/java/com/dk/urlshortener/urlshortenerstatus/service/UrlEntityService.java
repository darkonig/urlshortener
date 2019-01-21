package com.dk.urlshortener.urlshortenerstatus.service;

import com.dk.urlshortener.urlshortenerstatus.model.URLEntity;
import com.dk.urlshortener.urlshortenerstatus.model.URLEvent;

import java.util.List;

public interface UrlEntityService {

    URLEntity save(URLEntity entity);

    URLEvent saveEvent(URLEvent event);

    URLEntity findOne(String hash);

    List<URLEvent> findEvents(URLEntity entity, int page, int size);

    long countTotalEvents(URLEntity e);
}
