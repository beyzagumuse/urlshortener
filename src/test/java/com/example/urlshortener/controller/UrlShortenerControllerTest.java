package com.example.urlshortener.controller;

import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlShortenerController.class)
class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService service;

    @Test
    void createShortUrl_success() throws Exception {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortId("short123");
        shortUrl.setOriginalUrl("https://example.com");

        when(service.createShortUrl(anyString(), anyString(), anyLong())).thenReturn(shortUrl);

        mockMvc.perform(post("/api/urls")
                        .param("originalUrl", "https://example.com")
                        .param("customId", "short123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortId").value("short123"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"));
    }

    @Test
    void redirectToOriginalUrl_existingId_success() throws Exception {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl("https://example.com");

        when(service.getShortUrl("short123")).thenReturn(Optional.of(shortUrl));

        mockMvc.perform(get("/api/urls/short123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void redirectToOriginalUrl_nonExistingId_notFound() throws Exception {
        when(service.getShortUrl("invalid123")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/urls/invalid123"))
                .andExpect(status().isNotFound());
    }
}
