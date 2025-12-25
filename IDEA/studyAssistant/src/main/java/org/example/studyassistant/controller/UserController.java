package org.example.studyassistant.controller;

import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.example.studyassistant.pojo.dto.UserDTO;
import org.example.studyassistant.Impl.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    IUserService iUserService;

    @PostMapping("/login")
    public ResponseMessage<Map<String, Object>> login(@RequestBody UserDTO userDTO) {
        logger.info("🌐 收到登录请求，用户名: {}", userDTO.getName());
        logger.debug("📝 登录请求详情: {}", userDTO);
        
        try {
            // Service 现在直接返回 Map，包含了 token, role, name
            logger.info("🔍 开始调用用户服务进行登录验证...");
            Map<String, Object> loginResult = iUserService.login(userDTO);
            
            logger.info("✅ 登录成功，用户名: {}, 角色: {}", userDTO.getName(), loginResult.get("role"));
            logger.debug("🔑 Token信息: {}", loginResult.get("token"));
            logger.debug("👤 用户姓名: {}", loginResult.get("name"));
            
            // 构建响应消息
            ResponseMessage<Map<String, Object>> response = ResponseMessage.success(loginResult);
            logger.info("📤 准备返回响应，状态码: {}, 消息: {}", response.getCode(), response.getMessage());
            logger.debug("📦 响应数据: {}", response.getData());
            
            return response;
        } catch (RuntimeException e) {
            logger.error("❌ 登录失败，用户名: {}, 错误信息: {}", userDTO.getName(), e.getMessage());
            logger.debug("🔍 业务异常详情:", e);
            throw e; // 重新抛出异常，让全局异常处理器处理
        } catch (Exception e) {
            logger.error("💥 登录过程中发生未知错误，用户名: {}, 错误: {}", userDTO.getName(), e.getMessage(), e);
            throw new RuntimeException("登录失败，请稍后重试");
        }
    }

    @PostMapping("/register")
    public ResponseMessage<?> register(@RequestBody UserDTO userDTO) {
        // 调用 Service 层
        iUserService.register(userDTO);
        // 注册成功返回成功信息，不需要返回数据
        return ResponseMessage.success();
    }
}
