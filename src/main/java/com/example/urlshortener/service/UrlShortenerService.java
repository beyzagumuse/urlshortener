package com.example.urlshortener.service;

import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.repository.ShortUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UrlShortenerService {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);
    private final ShortUrlRepository repository;

    public UrlShortenerService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    public ShortUrl createShortUrl(String originalUrl, String customId, Long ttlSeconds) {
        String shortId = (customId != null) ? customId : UUID.randomUUID().toString().substring(0, 8);

        if (repository.findByShortId(shortId).isPresent()) {
            logger.warn("Short ID already exists: {}", shortId);
            throw new IllegalArgumentException("Short ID already in use");
        }

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortId(shortId);
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setCreatedAt(LocalDateTime.now());

        if (ttlSeconds != null) {
            shortUrl.setExpiresAt(LocalDateTime.now().plusSeconds(ttlSeconds));
        }

        repository.save(shortUrl);
        logger.info("Short URL created: {} -> {}", shortId, originalUrl);

        return shortUrl;
    }

    public Optional<ShortUrl> getShortUrl(String shortId) {
        return repository.findByShortId(shortId);
    }

    public ShortUrl resolveShortUrl(String shortId) {
        ShortUrl shortUrl = repository.findByShortId(shortId)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortId));

        logger.info("Redirecting {} to {}", shortId, shortUrl.getOriginalUrl());
        return shortUrl;
    }

    public void deleteShortUrl(String shortId) {
        if (!repository.findByShortId(shortId).isPresent()) {
            throw new UrlNotFoundException("Short URL not found: " + shortId);
        }

        repository.deleteByShortId(shortId);
        logger.info("Short URL deleted: {}", shortId);
    }
}
