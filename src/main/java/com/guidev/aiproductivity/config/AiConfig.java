package com.guidev.aiproductivity.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        Você é um assistente de produtividade.
                        Ajude o usuário a gerenciar suas tarefas.
                        """)
                .build();
    }
}