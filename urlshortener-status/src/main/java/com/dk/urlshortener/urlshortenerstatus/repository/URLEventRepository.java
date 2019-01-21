package com.dk.urlshortener.urlshortenerstatus.repository;

import com.dk.urlshortener.urlshortenerstatus.model.URLEntity;
import com.dk.urlshortener.urlshortenerstatus.model.URLEvent;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface URLEventRepository extends PagingAndSortingRepository<URLEvent, String> {

    List<URLEvent> findAllByEntity(URLEntity entity, PageRequest date);

    long countByEntity(URLEntity e);

}
