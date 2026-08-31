package com.guidev.aiproductivity.config;

import com.guidev.aiproductivity.tool.TaskTool;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider taskTools(TaskTool taskTool) {

        return MethodToolCallbackProvider
                .builder()
                .toolObjects(taskTool)
                .build();
    }
}