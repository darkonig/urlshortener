package com.dk.urlshortener.urlshortenerstatus.messeger;

import com.dk.urlshortener.urlshortenerstatus.exception.EntityNotFoundException;
import com.dk.urlshortener.urlshortenerstatus.messeger.domain.UrlNotificationStatus;
import com.dk.urlshortener.urlshortenerstatus.model.EventStatus;
import com.dk.urlshortener.urlshortenerstatus.model.URLEntity;
import com.dk.urlshortener.urlshortenerstatus.model.URLEvent;
import com.dk.urlshortener.urlshortenerstatus.service.UrlEntityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Profile("!disableQueue")
@Service
public class NotificationReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationReceiver.class);

    @Autowired
    private UrlEntityService service;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.url-access}")
    public void receiveUrlAccess(String payloadStr) throws IOException {
        LOGGER.info("received access payload='{}'", payloadStr);

        UrlNotificationStatus payload = objectMapper.readValue(payloadStr, UrlNotificationStatus.class);

        URLEntity entity = getEntity(payload);

        save(payload, entity, EventStatus.CREATED);
    }

    @KafkaListener(topics = "${kafka.topic.url-creation}")
    public void receiveUrlCreation(String payloadStr) throws IOException {
        LOGGER.info("received creation payload='{}'", payloadStr);

        UrlNotificationStatus payload = objectMapper.readValue(payloadStr, UrlNotificationStatus.class);

        URLEntity entity = getEntity(payload);

        save(payload, entity, EventStatus.ACCESS);
    }

    private void save(UrlNotificationStatus payload, URLEntity entity, EventStatus created) {
        service.saveEvent(URLEvent.builder()
                .ip(payload.getIp())
                .status(created)
                .entity(entity)
                .date(payload.getDate())
                .browser(payload.getBrowser())
                .browserVersion(payload.getBrowserVersion())
                .os(payload.getOs())
                .build());
    }

    private URLEntity getEntity(UrlNotificationStatus payload) {
        URLEntity entity;
        try {
            entity = service.findOne(payload.getHash());
        } catch (EntityNotFoundException e) {
            entity = URLEntity.builder()
                    .created(payload.getDate())
                    .hash(payload.getHash())
                    .origin(payload.getOrigin())
                    .build();

            entity = service.save(entity);
        }
        return entity;
    }
}
