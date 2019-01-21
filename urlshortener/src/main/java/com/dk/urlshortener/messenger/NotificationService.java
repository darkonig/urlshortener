package com.dk.urlshortener.messenger;

import com.dk.urlshortener.messenger.domain.UrlNotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Profile("!disableQueue")
@Service
public class NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @Value("${kafka.topic.url-access}")
    private String topicAccess;

    @Value("${kafka.topic.url-creation}")
    private String topicCreation;

    @Autowired
    private KafkaTemplate<String, UrlNotificationStatus> kafkaTemplate;

    public void notifyAccess(UrlNotificationStatus status) {
        LOGGER.info("[Access] Sending payload='{}' to topic='{}'", status, topicAccess);
        kafkaTemplate.send(topicAccess, status);
    }

    public void notifyCreation(UrlNotificationStatus status) {
        LOGGER.info("[Creation] Sending payload='{}' to topic='{}'", status, topicCreation);
        kafkaTemplate.send(topicCreation, status);
    }

}
