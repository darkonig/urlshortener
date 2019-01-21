package com.dk.urlshortener.urlshortenerstatus.controller;

import com.dk.urlshortener.urlshortenerstatus.model.EventStatus;
import com.dk.urlshortener.urlshortenerstatus.model.URLEntity;
import com.dk.urlshortener.urlshortenerstatus.model.URLEvent;
import com.dk.urlshortener.urlshortenerstatus.service.UrlEntityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UrlShortenerStatusController.class)
public class UrlShortenerStatusControllerTest {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlEntityService service;

    @Test
    public void getEntity_returnsObject() throws Exception {
        // given
        URLEntity entity = URLEntity.builder()
                .origin("http://google.com/")
                .hash("Abcd4255")
                .created(LocalDateTime.now())
                .build();
        given(service.findOne(anyString())).willReturn(entity);

        // do
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/url/" + entity.getHash())
                        .accept(MediaType.APPLICATION_JSON))

        // assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("hash").value(entity.getHash()));
    }

    @Test
    public void getEvents_returnsObject() throws Exception {
        // given
        URLEntity e = URLEntity.builder()
                .origin("http://google.com/")
                .hash("Abcd4255")
                .created(LocalDateTime.now())
                .build();

        List<URLEvent> events = new ArrayList<>();
        events.add(URLEvent.builder()
                .date(e.getCreated().plusDays(1))
                .entity(e)
                .status(EventStatus.ACCESS)
                .ip("192.168.0.2")
                .build());
        events.add(URLEvent.builder()
                .date(e.getCreated())
                .entity(e)
                .status(EventStatus.CREATED)
                .ip("192.168.0.1")
                .build());


        given(service.findEvents(any(), anyInt(), anyInt())).willReturn(events);

        // do
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/url/" + e.getHash() + "/events/")
                        .accept(MediaType.APPLICATION_JSON))

        // assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(events.get(0).getStatus().toString()))
                .andExpect(jsonPath("$[0].entity.hash").value(e.getHash()))
                .andExpect(jsonPath("$[1].status").value(events.get(1).getStatus().toString()));
    }
}
