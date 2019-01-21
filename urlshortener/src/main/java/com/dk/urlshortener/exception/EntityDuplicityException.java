package com.dk.urlshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityDuplicityException extends RuntimeException {

    public EntityDuplicityException(String msg) {
        super(msg);
    }

}
