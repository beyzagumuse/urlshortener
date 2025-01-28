package com.example.urlshortener.controller;

import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.service.UrlShortenerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/urls")
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShortUrl> createShortUrl(@RequestParam String originalUrl,
                                                   @RequestParam(required = false) String customId,
                                                   @RequestParam(required = false) Long ttlSeconds) {
        ShortUrl shortUrl = service.createShortUrl(originalUrl, customId, ttlSeconds);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortId}")
    public ResponseEntity<Object> redirectToOriginalUrl(@PathVariable String shortId) {
        return service.getShortUrl(shortId)
                .map(shortUrl -> ResponseEntity.status(302).location(URI.create(shortUrl.getOriginalUrl())).build())
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{shortId}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortId) {
        service.deleteShortUrl(shortId);
        return ResponseEntity.noContent().build();
    }
}
