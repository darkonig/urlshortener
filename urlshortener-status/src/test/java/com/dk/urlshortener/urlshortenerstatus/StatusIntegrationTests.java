package com.dk.urlshortener.urlshortenerstatus;

import com.dk.urlshortener.urlshortenerstatus.repository.URLEntityRepository;
import com.dk.urlshortener.urlshortenerstatus.repository.URLEventRepository;
import com.dk.urlshortener.urlshortenerstatus.model.EventStatus;
import com.dk.urlshortener.urlshortenerstatus.model.URLEntity;
import com.dk.urlshortener.urlshortenerstatus.model.URLEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("disableQueue")
//@DataMongoTest
public class StatusIntegrationTests {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private URLEntityRepository repository;

    @Autowired
    private URLEventRepository eventRepository;

    private URLEntity entity;
    private ArrayList<URLEvent> events;

    @Before
    public void setUp() {
        eventRepository.deleteAll();
        repository.deleteAll();

        entity = repository.save(URLEntity.builder()
                .hash("abc")
                .created(LocalDateTime.now())
                .origin("http://google.com/")
                .build());

        events = new ArrayList<>();
        events.add(URLEvent.builder()
                .date(entity.getCreated())
                .entity(entity)
                .status(EventStatus.CREATED)
                .ip("192.168.0.1")
                .build());
        events.add(URLEvent.builder()
                .date(entity.getCreated().plusDays(1))
                .entity(entity)
                .status(EventStatus.ACCESS)
                .ip("192.168.0.2")
                .build());
        eventRepository.saveAll(events);
    }

    @Test
    public void getEntity_returnsObject() {
        // request
        ResponseEntity<URLEntity> response = template.getForEntity("/api/v1/url/" + entity.getHash(), URLEntity.class);

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getHash()).isNotEmpty();
    }

    @Test
    public void getEvents_returnsObject() {
        // request
        ResponseEntity<List<URLEvent>> response = template.exchange("/api/v1/url/" + entity.getHash() + "/events/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<URLEvent>>(){});

        List<URLEvent> events = response.getBody();

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(events.size()).isEqualTo(this.events.size());
        assertThat(events.get(0).getStatus()).isEqualTo(this.events.get(1).getStatus());
        assertThat(events.get(1).getStatus()).isEqualTo(this.events.get(0).getStatus());
    }

}

