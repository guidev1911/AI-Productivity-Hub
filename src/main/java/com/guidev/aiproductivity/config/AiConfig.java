package com.guidev.aiproductivity.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder builder,
            ToolCallbackProvider taskTools) {

        return builder
                .defaultSystem("""
                        Você é um assistente de produtividade.
                        Ajude o usuário a gerenciar suas tarefas.
                        Utilize as ferramentas disponíveis quando necessário.
                        """)
                .defaultTools(taskTools)
                .build();
    }
}