package com.ruoyi.framework.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // 创建 RestTemplate 实例
        RestTemplate restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();

        // 获取现有的 HttpMessageConverter 列表
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();

        // 添加支持 text/event-stream 的 StringHttpMessageConverter
        // 该转换器可以处理文本内容流
        messageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }
}
