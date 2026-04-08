package com.shieldlm.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shieldlm.config.ApiModelProperties;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ApiModelClient implements ModelClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ApiModelProperties properties;

    public ApiModelClient(HttpClient httpClient, ObjectMapper objectMapper, ApiModelProperties properties) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public String generateReply(String prompt) {
        String apiKey = properties.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("缺少真实模型 API key 配置。");
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(buildUri())
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(buildRequestBody(prompt), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            return extractReply(response);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("模型调用失败: " + describeException(exception), exception);
        } catch (IOException exception) {
            throw new IllegalStateException("模型调用失败: " + describeException(exception), exception);
        }
    }

    private URI buildUri() {
        String baseUrl = trimTrailingSlash(properties.getBaseUrl());
        String chatPath = ensureLeadingSlash(properties.getChatPath());
        return URI.create(baseUrl + chatPath);
    }

    private String buildRequestBody(String prompt) throws IOException {
        JsonNode root = objectMapper.createObjectNode()
                .put("model", properties.getModelName())
                .set("messages", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("role", "user")
                                .put("content", prompt)));
        return objectMapper.writeValueAsString(root);
    }

    private String extractReply(HttpResponse<String> response) throws IOException {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("模型调用失败，状态码: " + response.statusCode() + "，响应: " + summarize(response.body()));
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        if (!contentNode.isTextual() || contentNode.asText().isBlank()) {
            throw new IllegalStateException("模型返回格式不符合预期。");
        }
        return contentNode.asText();
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("模型地址未配置。");
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String ensureLeadingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "/chat/completions";
        }
        return value.startsWith("/") ? value : "/" + value;
    }

    private String summarize(String value) {
        if (value == null || value.isBlank()) {
            return "<empty>";
        }
        return value.length() <= 200 ? value : value.substring(0, 200);
    }

    private String describeException(Exception exception) {
        String type = exception.getClass().getSimpleName();
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return type;
        }
        return type + " - " + message;
    }
}
