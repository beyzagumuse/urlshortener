package com.example.urlshortener.service;

import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlShortenerServiceTest {

    private final ShortUrlRepository repository = mock(ShortUrlRepository.class);
    private final UrlShortenerService service = new UrlShortenerService(repository);

    @Test
    void createShortUrl_withCustomId_success() {
        String originalUrl = "https://example.com";
        String customId = "custom123";

        when(repository.findByShortId(customId)).thenReturn(Optional.empty());

        ShortUrl result = service.createShortUrl(originalUrl, customId, null);

        assertEquals(customId, result.getShortId());
        assertEquals(originalUrl, result.getOriginalUrl());
        verify(repository, times(1)).save(any(ShortUrl.class));
    }

    @Test
    void createShortUrl_duplicateCustomId_throwsException() {
        String customId = "duplicate123";

        when(repository.findByShortId(customId)).thenReturn(Optional.of(new ShortUrl()));

        assertThrows(IllegalArgumentException.class, () ->
                service.createShortUrl("https://example.com", customId, null));
    }

    @Test
    void getShortUrl_existingId_success() {
        String shortId = "short123";
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortId(shortId);
        shortUrl.setOriginalUrl("https://example.com");

        when(repository.findByShortId(shortId)).thenReturn(Optional.of(shortUrl));

        Optional<ShortUrl> result = Optional.ofNullable(service.getShortUrl(shortId));

        assertTrue(result.isPresent());
        assertEquals(shortUrl.getOriginalUrl(), result.get().getOriginalUrl());
    }
}
