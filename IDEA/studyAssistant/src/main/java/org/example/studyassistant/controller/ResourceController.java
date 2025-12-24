package org.example.studyassistant.controller;

import org.example.studyassistant.Impl.IResourceService;
import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.example.studyassistant.pojo.entity.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resource")
@CrossOrigin(origins = "*")
public class ResourceController {
    @Autowired
    private IResourceService resourceService;

    // 1. 上传资源
    @PostMapping("/upload")
    public ResponseMessage<?> uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("courseId") Integer courseId,
                                         @RequestParam("resourceType") String resourceType) {
        // 直接调用Service层方法
        return resourceService.uploadFile(file, courseId, resourceType);
    }

    // 2. 获取某课程的资源列表
    @GetMapping("/list/{courseId}")
    public ResponseMessage<List<Resource>> listResources(@PathVariable Integer courseId) {
        // 直接调用Service层方法
        return resourceService.listResources(courseId);
    }

    // 3. 下载资源
    @GetMapping("/download/{resourceId}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable Integer resourceId) {
        // 直接调用Service层方法
        return resourceService.downloadFile(resourceId);
    }
}
