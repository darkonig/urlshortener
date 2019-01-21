package com.dk.urlshortener.urlshortenerstatus.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class URLEvent {

    @Id
    private String id;

    @DBRef
    private URLEntity entity;

    private String ip;

    private EventStatus status;

    private LocalDateTime date;

    private String browser;

    private String browserVersion;

    private String os;

}
