package com.dk.urlshortener.service;

import com.dk.urlshortener.model.URLEntity;

public interface UrlEntityService {

    URLEntity findOne(String id);

    URLEntity save(String url);

}
