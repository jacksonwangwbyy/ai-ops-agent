package com.example.aiopsagent.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class AgentConfig {

    @Bean
    ChatClient opsChatClient(ChatClient.Builder builder, ResourceLoader resourceLoader) {
        return builder.defaultSystem(loadPrompt(resourceLoader)).build();
    }

    private String loadPrompt(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("classpath:/prompts/ops-agent-system.txt");
        try (var inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (IOException exception) {
            throw new IllegalStateException("Failed to load system prompt", exception);
        }
    }
}
