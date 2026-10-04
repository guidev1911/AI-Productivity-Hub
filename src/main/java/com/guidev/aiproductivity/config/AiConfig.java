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
                
                        Ajude o usuário a gerenciar suas tarefas utilizando somente
                        as ferramentas disponíveis.
                
                        Nunca invente ferramentas, funcionalidades ou ações que o sistema
                        não possui.
                
                        Nunca diga que o usuário receberá lembretes, notificações ou avisos
                        automáticos, pois esse recurso não existe.
                
                        Após executar uma ação, informe somente o que realmente foi executado
                        com base no resultado da ferramenta.
                
                        Utilize as ferramentas disponíveis quando necessário.
                        """)
                .defaultTools(taskTools)
                .build();
    }
}