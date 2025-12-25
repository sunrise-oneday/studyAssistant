package org.example.studyassistant.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.utils.JsonUtils;
import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    public ResponseMessage<Map<String, Object>> chat(String prompt, String system) {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return new ResponseMessage<>(500, "缺少 DashScope API Key");
        }

        List<Message> messages = new ArrayList<>();
        if (system != null && !system.isEmpty()) {
            messages.add(Message.builder().role("system").content(system).build());
        }
        messages.add(Message.builder().role("user").content(prompt).build());

        Generation generation = new Generation();
        GenerationParam param = GenerationParam.builder()
                .model("deepseek-v3.2-exp")
                .messages(messages)
                .apiKey(apiKey)
                .build();
        try {
            GenerationResult result = generation.call(param);
            String raw = JsonUtils.toJson(result);
            String text = result.getOutput() != null && result.getOutput().getChoices() != null
                    ? result.getOutput().getChoices().get(0).getMessage().getContent()
                    : null;


            Map<String, Object> data = new HashMap<>();
            if (text != null && !text.isEmpty()) {
                data.put("text", text);
            }
            data.put("raw", raw);
            return ResponseMessage.success(data);
        } catch (ApiException e) {
            return new ResponseMessage<>(e.getStatus() != null ? e.getStatus().getStatusCode() : 500,
                    e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseMessage<>(500, "AI 服务调用失败: " + e.getMessage(), null);
        }
    }

    private String getApiKey() {
        String key = System.getenv("DASHSCOPE_API_KEY");
        if (key == null || key.isEmpty()) {
            key = System.getenv("AI_DASHSCOPE_API_KEY");
        }
        return key;
    }
}
