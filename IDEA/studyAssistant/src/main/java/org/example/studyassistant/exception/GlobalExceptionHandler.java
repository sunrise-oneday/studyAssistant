package org.example.studyassistant.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    Logger logger=LoggerFactory.getLogger(GlobalAdvisorAdapterRegistry.class);
        @ExceptionHandler({Exception.class})
    public ResponseMessage handlerException(Exception e, HttpServletRequest request, HttpServletResponse response){
            logger.error("❌ 全局异常处理 - 请求: {} {}, 错误信息: {}", request.getMethod(), request.getRequestURL(), e.getMessage(), e);
            logger.debug("🔍 异常详情:", e);
            
            if (e instanceof RuntimeException) { // 假设你的业务异常都继承自 RuntimeException
                logger.warn("⚠️ 业务异常: {}", e.getMessage());
                return new ResponseMessage<>(400, e.getMessage(), null); // 客户端错误
            }
            
            logger.error("💥 系统内部错误: {}", e.getMessage(), e);
            return new ResponseMessage<>(500, "服务器内部错误", null); // 服务器内部错误
        }
}
