package com.dk.urlshortener.repository;

import com.dk.urlshortener.EmbeddedRedisServer;
import com.dk.urlshortener.model.URLEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("disableQueue")
public class UrlEntityRepositoryTest {

    @Autowired
    private UrlEntityRepository repository;

    /*
     * It is need to have a Redis server instance available,
     * Start/Stop an embedded instance or reuse an already running local installation
     */
    public static @ClassRule
    RuleChain rules = RuleChain
            .outerRule(EmbeddedRedisServer.runningAt(6379).suppressExceptions());

    @Autowired
    RedisOperations<Object, Object> operations;

    URLEntity entity = URLEntity.builder()
            .origin("http://google.com/")
            .hash("abc")
            .build();

    @Before
    @After
    public void setUp() {
        operations.execute((RedisConnection connection) -> {
            connection.flushDb();
            return "OK";
        });
    }

    @Test
    public void findByOrigin() {
        URLEntity saved = repository.save(entity);
        Optional<URLEntity> search = repository.findByOrigin(entity.getOrigin());

        assertThat(search).isPresent();
        assertThat(search.get().getOrigin()).isEqualTo(saved.getOrigin());
        assertThat(search.get().getHash()).isEqualTo(saved.getHash());
    }

    @Test
    public void findByOrigin_returnsEmpty() {
        Optional<URLEntity> search = repository.findByOrigin(entity.getOrigin());

        assertThat(search).isEmpty();
    }

}
