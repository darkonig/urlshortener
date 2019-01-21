package com.dk.urlshortener.controller;

import com.dk.urlshortener.exception.EntityNotFoundException;
import com.dk.urlshortener.model.URLRequest;
import com.dk.urlshortener.model.URLEntity;
import com.dk.urlshortener.service.UrlEntityService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UrlShortenerController.class)
public class UrlShortenerControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlEntityService service;

    @Test
    public void insertUrl_returnsShortUrl() throws Exception {
        // given
        URLEntity entity = URLEntity.builder()
                .origin("http://google.com/")
                .hash("http://abc.ly/Abcd4255")
                .build();
        given(service.save(anyString())).willReturn(entity);

        // do
        URLRequest request = URLRequest.builder()
                .url("http://google.com/")
                .build();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(request)))

        // assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("hash").value(entity.getHash()));
    }

    @Test
    public void getUrl_throwsNotFound() throws Exception {
        // given
        given(service.findOne(anyString())).willThrow(new EntityNotFoundException("Error"));

        // do
        mockMvc.perform(
                MockMvcRequestBuilders.get("/abc")
                        .accept(MediaType.APPLICATION_JSON))
        // assert
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").isNotEmpty());
    }

    public String json(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

}
