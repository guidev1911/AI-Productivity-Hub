package com.guidev.aiproductivity.config;

import com.guidev.aiproductivity.tool.TaskTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallback[] taskTools(TaskTool taskTool) {

        return MethodToolCallbackProvider
                .builder()
                .toolObjects(taskTool)
                .build()
                .getToolCallbacks();
    }
}