package com.dk.urlshortener;

import com.dk.urlshortener.exception.ApiError;
import com.dk.urlshortener.model.URLRequest;
import com.dk.urlshortener.model.URLEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("disableQueue")
public class IntegrationTests {

    /*
     * It is need to have a Redis server instance available,
     * Start/Stop an embedded instance or reuse an already running local installation
     */
    public static @ClassRule
    RuleChain rules = RuleChain
            .outerRule(EmbeddedRedisServer.runningAt(6379).suppressExceptions());
    @Autowired
    RedisOperations<Object, Object> operations;

    @Autowired
    private TestRestTemplate template;

    @Value("${app.site.name}")
    private String siteName;

    @Before
    @After
    public void setUp() {
        // disable auto redirect
        template.getRestTemplate().setRequestFactory(requestFactory());

        operations.execute((RedisConnection connection) -> {
            connection.flushDb();
            return "OK";
        });
    }

    //@Bean
    public ClientHttpRequestFactory requestFactory() {
        // Disable auto redirect on 3xx HTTP responses
        CloseableHttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Test
    public void insertUrl_returnsObject() {
        URLRequest request = URLRequest.builder()
                .url("http://google.com/")
                .build();

        // request
        ResponseEntity<URLEntity> response = template.postForEntity("/", request, URLEntity.class);

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getHash()).isNotEmpty();
    }

    @Test
    public void getUrl_returnsObject() {
        URLRequest request = URLRequest.builder()
                .url("http://google.com/")
                .build();

        URLEntity saved;
        {
            // request
            ResponseEntity<URLEntity> response = template.postForEntity("/", request, URLEntity.class);

            saved = response.getBody();

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(saved.getHash()).isNotEmpty();
        }

        String hash = saved.getHash().substring(saved.getHash().lastIndexOf("/") + 1);
        // request
        ResponseEntity<URLEntity> response = template.getForEntity("/get/" + hash, URLEntity.class);

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getHash()).isEqualTo(hash);
        assertThat(response.getBody().getOrigin()).isEqualTo(saved.getOrigin());
    }

    @Test
    public void getUrl_throwsError() {
        // request
        ResponseEntity<ApiError> response = template.getForEntity("/get/abc", ApiError.class);

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isNotEmpty();
    }

    @Test
    public void redirectTo_returnObject() {
        URLRequest request = URLRequest.builder()
                .url("http://google.com/")
                .build();

        URLEntity saved;
        {
            // request
            ResponseEntity<URLEntity> response = template.postForEntity("/", request, URLEntity.class);

            saved = response.getBody();

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(saved.getHash()).isNotEmpty();
        }

        // request
        ResponseEntity<String> response = template.getForEntity("/" +
                saved.getHash().substring(saved.getHash().lastIndexOf("/") + 1), String.class);

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TEMPORARY_REDIRECT);
        assertThat(response.getHeaders().getLocation().toString()).isEqualTo(saved.getOrigin());
    }

}

