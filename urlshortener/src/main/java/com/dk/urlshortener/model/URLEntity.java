package com.dk.urlshortener.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@RedisHash("urls")
public class URLEntity {

    @Id
    private String hash;

    @Indexed
    private String origin;

    private LocalDateTime created;

}
