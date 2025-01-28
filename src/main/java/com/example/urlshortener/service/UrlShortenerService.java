package com.example.urlshortener.service;

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

        // Check if shortId already exists
        if (repository.findByShortId(shortId).isPresent()) {
            logger.warn("Short ID already in use: {}", shortId);
            throw new IllegalArgumentException("Short ID already in use");
        }

        // Create and save ShortUrl
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortId(shortId);
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setCreatedAt(LocalDateTime.now());
        if (ttlSeconds != null) {
            shortUrl.setExpiresAt(LocalDateTime.now().plusSeconds(ttlSeconds));
        }

        ShortUrl savedUrl = repository.save(shortUrl);
        logger.info("Short URL created: {}", savedUrl.getShortId());
        return savedUrl;
    }

    public Optional<ShortUrl> getShortUrl(String shortId) {
        logger.info("Fetching Short URL for ID: {}", shortId);
        return repository.findByShortId(shortId);
    }

    public void deleteShortUrl(String shortId) {
        logger.info("Deleting Short URL with ID: {}", shortId);
        repository.deleteByShortId(shortId);
    }
}
