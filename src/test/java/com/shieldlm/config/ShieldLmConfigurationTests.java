package com.shieldlm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shieldlm.model.ApiModelClient;
import com.shieldlm.model.MockModelClient;
import com.shieldlm.model.ModelClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShieldLmConfigurationTests {

    private final ShieldLmConfiguration configuration = new ShieldLmConfiguration();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createsApiModelClientWhenModeIsApi() {
        ApiModelProperties properties = new ApiModelProperties();
        properties.setMode("api");
        properties.setApiKey("test-api-key");

        ModelClient client = configuration.modelClient(properties, objectMapper);

        assertThat(client).isInstanceOf(ApiModelClient.class);
    }

    @Test
    void createsMockModelClientWhenModeIsMock() {
        ApiModelProperties properties = new ApiModelProperties();
        properties.setMode("mock");

        ModelClient client = configuration.modelClient(properties, objectMapper);

        assertThat(client).isInstanceOf(MockModelClient.class);
    }

    @Test
    void rejectsApiModeWhenApiKeyIsMissing() {
        ApiModelProperties properties = new ApiModelProperties();
        properties.setMode("api");
        properties.setApiKey("  ");

        assertThatThrownBy(() -> configuration.modelClient(properties, objectMapper))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("API key");
    }
}
