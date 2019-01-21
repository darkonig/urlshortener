package com.dk.urlshortener;

import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

public class EmbeddedRedisServer extends ExternalResource {

    private static final int DEFAULT_PORT = 6379;
    private RedisServer server;
    private int port = DEFAULT_PORT;
    private boolean suppressExceptions = false;

    public EmbeddedRedisServer() { }

    protected EmbeddedRedisServer(int port) {
        this.port = port;
    }

    public static EmbeddedRedisServer runningAt(Integer port) {
        return new EmbeddedRedisServer(port != null ? port : DEFAULT_PORT);
    }

    public EmbeddedRedisServer suppressExceptions() {
        this.suppressExceptions = true;
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.junit.rules.ExternalResource#before()
     */
    @Override
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
    @Override
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