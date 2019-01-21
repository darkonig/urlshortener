package com.dk.urlshortener.config;

import com.dk.urlshortener.constanst.Constants;
import com.dk.urlshortener.model.URLEntity;
import com.dk.urlshortener.service.UrlEntityService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile(Constants.PROFILE_DEV)
@Component
public class Startup implements ApplicationRunner {
    private UrlEntityService service;

    public Startup(UrlEntityService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        URLEntity save = service.save("https://google.com");

        System.out.println("Saved " + save.getOrigin() + " - " + save.getHash());

        save = service.save("https://gmail.com");

        System.out.println("Saved " + save.getOrigin() + " - " + save.getHash());
    }
}
