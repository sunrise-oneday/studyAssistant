package org.example.studyassistant.AI_Assistant;

import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/chat")
    public ResponseMessage<?> chat(@RequestBody Map<String, Object> params) {
        String prompt = params.get("prompt") == null ? "" : String.valueOf(params.get("prompt"));
        String system = params.get("system") == null ? "" : String.valueOf(params.get("system"));
        return aiService.chat(prompt, system);
    }
}

