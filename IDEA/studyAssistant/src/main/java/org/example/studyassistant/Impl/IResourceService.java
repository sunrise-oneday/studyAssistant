package org.example.studyassistant.Impl;

import jakarta.transaction.Transactional;
import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.example.studyassistant.pojo.entity.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IResourceService {

    @Transactional
    ResponseMessage<?> uploadFile(MultipartFile file, Integer courseId, String resourceType);

    ResponseMessage<List<Resource>> listResources(Integer courseId);

    ResponseEntity<org.springframework.core.io.Resource> downloadFile(Integer resourceId);
}
