package com.dk.urlshortener.service;

import com.dk.urlshortener.exception.EntityDuplicityException;
import com.dk.urlshortener.exception.EntityNotFoundException;
import com.dk.urlshortener.model.URLEntity;
import com.dk.urlshortener.repository.UrlEntityRepository;
import com.dk.urlshortener.service.impl.UrlEntityServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UrlEntityServiceTest {
    @Mock
    private UrlEntityRepository repository;

    private UrlEntityService service;

    private URLEntity entity;

    @Before
    public void setUp() {
        service = new UrlEntityServiceImpl(repository);

        entity = URLEntity.builder()
                .origin("http://google.com/")
                .hash("abc")
                .build();
    }

    @Test
    public void findOne_returnsEntity() throws Exception {
        // given
        given(repository.findById(anyString())).willReturn(
                Optional.of(entity));

        // do
        URLEntity one = service.findOne("abc");

        // assert
        assertThat(one).isNotNull();
        assertThat(one.getOrigin()).isEqualTo(entity.getOrigin());
        assertThat(one.getHash()).isEqualTo(entity.getHash());
    }

    @Test(expected = EntityNotFoundException.class)
    public void findOne_throwsError() throws Exception {
        // given
        given(repository.findById(anyString())).willReturn(
                Optional.empty());

        // do
        service.findOne("abc");
    }

    @Test
    public void save_success() {
        // given
        given(repository.findByOrigin(anyString())).willReturn(Optional.empty());
        given(repository.save(any())).willReturn(entity);

        // do
        URLEntity saved = service.save(entity.getOrigin());

        // assert
        assertThat(saved.getOrigin()).isEqualTo(entity.getOrigin());
        assertThat(saved.getHash()).isEqualTo(entity.getHash());
    }

    @Test(expected = EntityDuplicityException.class)
    public void save_throwError() {
        // given
        given(repository.findByOrigin(anyString())).willReturn(Optional.of(entity));

        // do
        URLEntity saved = service.save(entity.getOrigin());
    }
}