package com.example.urlshortener.controller;

import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UrlShortenerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UrlShortenerService service;

    @InjectMocks
    private UrlShortenerController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

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
