package com.ruoyi.web.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import com.fasterxml.jackson.core.type.TypeReference;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dify")
public class ApiController {
    @Value("${api.bearer.token}")
    private String BearerToken;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String DIFY_API_URL = "https://dify.aipfuture.com/v1/workflows/run";

    public ApiController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    @PostMapping("/workflow/run")
    public AjaxResult runWorkflow(@RequestBody Map<String, String> request) {
        try {
            // 配置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + BearerToken);

            // 配置请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", Map.of("code", request.get("code")));
            requestBody.put("response_mode", "streaming");
            requestBody.put("user", "abc-123");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求并接收响应
            ResponseEntity<String> response = restTemplate.exchange(
                    DIFY_API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 获取响应体
            String responseBody = response.getBody();
            if (responseBody == null) {
                return AjaxResult.error("响应为空");
            }

            // 按 "data:" 分割响应体以获取每个块
            String[] dataChunks = responseBody.split("data:");
            ObjectMapper objectMapper = new ObjectMapper();

            // 遍历每个块以查找 "workflow_finished" 事件
            for (String chunk : dataChunks) {
                chunk = chunk.trim();
                if (!chunk.isEmpty()) {
                    // 尝试将块解析为 JSON 对象
                    try {
                        Map<String, Object> jsonData = objectMapper.readValue(chunk, new TypeReference<Map<String, Object>>() {});
                        if ("workflow_finished".equals(jsonData.get("event"))) {
                            // 找到 "workflow_finished" 事件，返回结果
                            return AjaxResult.success("操作成功", jsonData);
                        }
                    } catch (Exception e) {
                        System.err.println("JSON 解析失败: " + chunk);
                        e.printStackTrace();
                    }
                }
            }

            // 如果没有找到 "workflow_finished" 事件
            return AjaxResult.error("未找到 workflow_finished 事件");

        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("调用 Dify API 失败: " + e.getMessage());
        }
    }

}
