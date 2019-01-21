package com.dk.urlshortener.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class URLRequest {

    private String url;

}
