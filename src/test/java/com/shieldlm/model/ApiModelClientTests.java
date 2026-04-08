package com.shieldlm.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shieldlm.config.ApiModelProperties;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiModelClientTests {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<HttpServer> servers = new ArrayList<>();

    @AfterEach
    void tearDown() {
        servers.forEach(server -> server.stop(0));
    }

    @Test
    void sendsOpenAiCompatibleRequestAndParsesReply() throws Exception {
        AtomicReference<String> authorizationHeader = new AtomicReference<>();
        AtomicReference<String> requestPath = new AtomicReference<>();
        AtomicReference<JsonNode> requestBody = new AtomicReference<>();

        HttpServer server = startServer(exchange -> {
            authorizationHeader.set(exchange.getRequestHeaders().getFirst("Authorization"));
            requestPath.set(exchange.getRequestURI().getPath());
            requestBody.set(readJson(exchange));
            writeJson(exchange, 200, """
                    {
                      "choices": [
                        {
                          "message": {
                            "content": "这是一条真实模型回复"
                          }
                        }
                      ]
                    }
                    """);
        });

        ApiModelClient client = new ApiModelClient(HttpClient.newHttpClient(), objectMapper, properties(server));

        String reply = client.generateReply("请简要说明退款流程");

        assertThat(reply).isEqualTo("这是一条真实模型回复");
        assertThat(authorizationHeader.get()).isEqualTo("Bearer test-api-key");
        assertThat(requestPath.get()).isEqualTo("/v1/chat/completions");
        assertThat(requestBody.get().path("model").asText()).isEqualTo("gpt5.4");
        assertThat(requestBody.get().path("messages")).hasSize(1);
        assertThat(requestBody.get().path("messages").get(0).path("role").asText()).isEqualTo("user");
        assertThat(requestBody.get().path("messages").get(0).path("content").asText()).isEqualTo("请简要说明退款流程");
    }

    @Test
    void throwsClearExceptionWhenResponseStatusIsNotSuccessful() throws Exception {
        HttpServer server = startServer(exchange -> writeJson(exchange, 401, """
                {
                  "error": {
                    "message": "invalid api key"
                  }
                }
                """));

        ApiModelClient client = new ApiModelClient(HttpClient.newHttpClient(), objectMapper, properties(server));

        assertThatThrownBy(() -> client.generateReply("你好"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("401")
                .hasMessageContaining("invalid api key");
    }

    @Test
    void throwsClearExceptionWhenResponseContentIsMissing() throws Exception {
        HttpServer server = startServer(exchange -> writeJson(exchange, 200, """
                {
                  "choices": [
                    {
                      "message": {}
                    }
                  ]
                }
                """));

        ApiModelClient client = new ApiModelClient(HttpClient.newHttpClient(), objectMapper, properties(server));

        assertThatThrownBy(() -> client.generateReply("你好"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("模型返回格式不符合预期");
    }

    @Test
    void includesExceptionTypeWhenTransportErrorHasNoMessage() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException());

        ApiModelClient client = new ApiModelClient(httpClient, objectMapper, properties());

        assertThatThrownBy(() -> client.generateReply("你好"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("IOException");
    }

    private HttpServer startServer(ExchangeHandler handler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/v1/chat/completions", exchange -> {
            try {
                handler.handle(exchange);
            } finally {
                exchange.close();
            }
        });
        server.start();
        servers.add(server);
        return server;
    }

    private ApiModelProperties properties(HttpServer server) {
        ApiModelProperties properties = new ApiModelProperties();
        properties.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
        properties.setModelName("gpt5.4");
        properties.setApiKey("test-api-key");
        properties.setChatPath("/v1/chat/completions");
        return properties;
    }

    private ApiModelProperties properties() {
        ApiModelProperties properties = new ApiModelProperties();
        properties.setBaseUrl("https://ai.td.ee");
        properties.setModelName("gpt5.4");
        properties.setApiKey("test-api-key");
        properties.setChatPath("/v1/chat/completions");
        return properties;
    }

    private JsonNode readJson(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return objectMapper.readTree(inputStream);
        }
    }

    private void writeJson(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    @FunctionalInterface
    private interface ExchangeHandler {
        void handle(HttpExchange exchange) throws IOException;
    }
}
