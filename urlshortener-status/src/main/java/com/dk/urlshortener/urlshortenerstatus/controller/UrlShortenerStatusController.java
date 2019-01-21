package com.dk.urlshortener.urlshortenerstatus.controller;

import com.dk.urlshortener.urlshortenerstatus.model.URLEntity;
import com.dk.urlshortener.urlshortenerstatus.model.URLEvent;
import com.dk.urlshortener.urlshortenerstatus.model.URLStatus;
import com.dk.urlshortener.urlshortenerstatus.service.UrlEntityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/url/")
public class UrlShortenerStatusController {

    private UrlEntityService service;

    public UrlShortenerStatusController(UrlEntityService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public URLEntity getEntity(@PathVariable String id) {
        return service.findOne(id);
    }

    // list last 10 events
    @GetMapping("{id}/events/")
    public List<URLEvent> getEvents(@PathVariable String id) {
        URLEntity e = service.findOne(id);

        return service.findEvents(e, 0, 10);
    }

    @GetMapping("{id}/status/")
    public URLStatus getStatus(@PathVariable String id) {
        URLEntity e = service.findOne(id);

        return URLStatus.builder().totalAccess(service.countTotalEvents(e))
                .topBrowsers(Arrays.asList("Not implemented.")).build();
    }
}
