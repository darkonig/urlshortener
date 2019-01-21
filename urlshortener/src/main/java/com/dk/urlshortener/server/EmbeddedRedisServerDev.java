package com.dk.urlshortener.server;

import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

public class EmbeddedRedisServerDev {

    private static final int DEFAULT_PORT = 6379;
    private RedisServer server;
    private int port = DEFAULT_PORT;
    private boolean suppressExceptions = false;

    public EmbeddedRedisServerDev() { }

    protected EmbeddedRedisServerDev(int port) {
        this.port = port;
    }

    public static EmbeddedRedisServerDev runningAt(Integer port) {
        return new EmbeddedRedisServerDev(port != null ? port : DEFAULT_PORT);
    }

    /*
     * (non-Javadoc)
     * @see org.junit.rules.ExternalResource#before()
     */
    @PostConstruct
    protected void before() throws IOException {
        try {
            this.server = new RedisServer(this.port);
            this.server.start();
        } catch (Exception e) {
            if (!suppressExceptions) {
                throw e;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.junit.rules.ExternalResource#after()
     */
    @PreDestroy
    protected void after() {
        try {
            this.server.stop();
        } catch (Exception e) {
            if (!suppressExceptions) {
                throw e;
            }
        }
    }
}
