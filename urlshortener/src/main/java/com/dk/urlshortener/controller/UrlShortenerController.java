package com.dk.urlshortener.controller;

import com.dk.urlshortener.messenger.NotificationService;
import com.dk.urlshortener.messenger.domain.UrlNotificationStatus;
import com.dk.urlshortener.model.URLRequest;
import com.dk.urlshortener.model.URLEntity;
import com.dk.urlshortener.service.UrlEntityService;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class UrlShortenerController {

    private UrlEntityService service;
    private Optional<NotificationService> notifyService;
    private String siteName;

    public UrlShortenerController(UrlEntityService service
            , @Value("${app.site.name}") String siteName) {
        this.service = service;
        this.notifyService = Optional.empty();
        this.siteName = siteName;
    }

    @Autowired(required = false)
    public void setNotifyService(Optional<NotificationService> notifyService) {
        this.notifyService = notifyService;
    }

    @PostMapping()
    public URLEntity insertUrl(@RequestBody URLRequest url,
                               HttpServletRequest request) {
        URLEntity e = service.save(url.getUrl());
        if (e == null) {
            throw new InternalException("Unexpected exception.");
        }

        notifyCreation(request, e);

        e.setHash(siteName + "/" + e.getHash());

        return e;
    }

    @GetMapping("get/{id}")
    public URLEntity getUrl(@PathVariable String id) {
        return service.findOne(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id,
                                        HttpServletRequest request) {
        URLEntity l = service.findOne(id);
        if (l != null) {
            notifyAccess(request, l);

            HttpHeaders h = new HttpHeaders();
            h.setLocation(URI.create(l.getOrigin()));
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers(h).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void notifyCreation(HttpServletRequest request, URLEntity e) {
        if (!notifyService.isPresent()) return;

        try {
            String ip = request.getHeader("X-FORWARDED-FOR");
            if (StringUtils.isEmpty(ip)) {
                ip = request.getRemoteAddr();
            }

            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

            notifyService.get().notifyCreation(UrlNotificationStatus.builder()
                    .date(e.getCreated())
                    .hash(e.getHash())
                    .origin(e.getOrigin())
                    .ip(ip)
                    .browser(userAgent.getBrowser().getName())
                    .browserVersion(userAgent.getBrowserVersion() != null ? userAgent.getBrowserVersion().getVersion() : "")
                    .os(userAgent.getOperatingSystem().getName())
                    .build());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void notifyAccess(HttpServletRequest request, URLEntity l) {
        if (!notifyService.isPresent()) return;

        try {
            String ip = request.getHeader("X-FORWARDED-FOR");
            if (StringUtils.isEmpty(ip)) {
                ip = request.getRemoteAddr();
            }

            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

            notifyService.get().notifyAccess(UrlNotificationStatus.builder()
                    .date(LocalDateTime.now())
                    .hash(l.getHash())
                    .origin(l.getOrigin())
                    .ip(ip)
                    .browser(userAgent.getBrowser().getName())
                    .browserVersion(userAgent.getBrowserVersion() != null ? userAgent.getBrowserVersion().getVersion() : "")
                    .os(userAgent.getOperatingSystem().getName())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
