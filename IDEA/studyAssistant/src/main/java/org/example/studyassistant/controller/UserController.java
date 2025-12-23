package org.example.studyassistant.controller;

import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.example.studyassistant.pojo.dto.UserDTO;
import org.example.studyassistant.Impl.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    IUserService iUserService;

    @PostMapping("/login")
    public ResponseMessage<Map<String, Object>> login(@RequestBody UserDTO userDTO) {
        // Service 现在直接返回 Map，包含了 token, role, name
        Map<String, Object> loginResult = iUserService.login(userDTO);
        return ResponseMessage.success(loginResult);
    }

    @PostMapping("/register")
    public ResponseMessage<?> register(@RequestBody UserDTO userDTO) {
        // 调用 Service 层
        iUserService.register(userDTO);
        // 注册成功返回成功信息，不需要返回数据
        return ResponseMessage.success();
    }
}
