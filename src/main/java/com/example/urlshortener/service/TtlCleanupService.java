package com.example.urlshortener.service;

import com.example.urlshortener.repository.ShortUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TtlCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(TtlCleanupService.class);
    private final ShortUrlRepository repository;

    public TtlCleanupService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void cleanUpExpiredUrls() {
        logger.info("Starting TTL cleanup...");

        long countBefore = repository.count();

        repository.findAll().stream()
                .filter(shortUrl -> shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(LocalDateTime.now()))
                .forEach(shortUrl -> {
                    logger.info("Deleting expired URL with ID: {}", shortUrl.getShortId());
                    repository.deleteById(shortUrl.getId());
                });

        long countAfter = repository.count();
        logger.info("TTL cleanup finished. Total deleted: {}", countBefore - countAfter);
    }
}
