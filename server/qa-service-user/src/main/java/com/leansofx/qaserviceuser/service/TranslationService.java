package com.leansofx.qaserviceuser.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TranslationService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Map<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    public TranslationService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String translate(String text, String targetLang) {
        if (text == null || text.isEmpty() || "zh-CN".equals(targetLang)) {
            return text;
        }

        text = text.replaceAll("<[^>]*>", "").trim();

        String langCode = "en".equals(targetLang) || "en-US".equals(targetLang) ? "en-US" : targetLang;

        Map<String, String> langCache = cache.computeIfAbsent(langCode, k -> new ConcurrentHashMap<>());

        if (langCache.containsKey(text)) {
            return langCache.get(text);
        }

        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String fullUrl = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=zh%7C" + langCode;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .header("User-Agent", "Mozilla/5.0")
                    .timeout(java.time.Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                String translatedText = root.path("responseData").path("translatedText").asText();

                if (translatedText != null && !translatedText.isEmpty()) {
                    langCache.put(text, translatedText);
                    return translatedText;
                }
            }
        } catch (Exception e) {
            System.err.println("Translation error for '" + text + "': " + e.getMessage());
        }

        return text;
    }

    public String translateExperience(String experience, String targetLang) {
        if (experience == null || experience.isEmpty() || "zh-CN".equals(targetLang)) {
            return experience;
        }

        String result = experience
                .replace("年", " years")
                .replace("临床经验", " clinical experience");
        return result;
    }

    public java.util.List<String> translateList(java.util.List<String> texts, String targetLang) {
        return texts.stream().map(t -> translate(t, targetLang)).collect(java.util.stream.Collectors.toList());
    }
}
