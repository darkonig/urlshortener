package com.dk.urlshortener.urlshortenerstatus.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class URLStatus {

    private long totalAccess;

    private List<String> topBrowsers;

}
