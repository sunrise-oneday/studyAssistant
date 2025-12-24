package org.example.studyassistant.service;

import jakarta.transaction.Transactional;
import org.example.studyassistant.Impl.IResourceService;
import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.example.studyassistant.pojo.entity.Course;
import org.example.studyassistant.pojo.entity.Resource;
import org.example.studyassistant.repository.CourseRepository;
import org.example.studyassistant.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ResourceService implements IResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private CourseRepository courseRepository;

    private final Path fileStorageLocation;

    // 支持的文件类型
    private static final List<String> ALLOWED_RESOURCE_TYPES = List.of("PPT", "VIDEO", "DOCUMENT", "OTHER");

    // 支持的文件扩展名
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            // 文档类型
            ".pdf", ".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx", ".txt",
            // 视频类型
            ".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv",
            // 图片类型
            ".jpg", ".jpeg", ".png", ".gif"
    );

    public ResourceService() {
        this.fileStorageLocation = Paths.get("./uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建上传目录", ex);
        }
    }

    @Transactional
    @Override
    public ResponseMessage<?> uploadFile(MultipartFile file, Integer courseId, String resourceType) {
        try {
            // 1. 验证文件类型
            if (!ALLOWED_RESOURCE_TYPES.contains(resourceType)) {
                return new ResponseMessage<>(400, "不支持的资源类型: " + resourceType);
            }

            // 2. 检查课程
            Course course = courseRepository.findById(courseId).orElse(null);
            if (course == null) return new ResponseMessage<>(404, "课程不存在");

            // 3. 验证文件
            if (file.isEmpty()) {
                return new ResponseMessage<>(400, "文件不能为空");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return new ResponseMessage<>(400, "文件名无效");
            }

            // 4. 验证文件扩展名
            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }

            if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                return new ResponseMessage<>(400, "不支持的文件格式: " + fileExtension);
            }

            // 5. 生成唯一文件名 (防止重名覆盖)
            String storedFileName = UUID.randomUUID().toString() + fileExtension;

            // 6. 保存文件到磁盘
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            file.transferTo(targetLocation.toFile());

            // 7. 保存记录到数据库
            Resource resource = new Resource();
            resource.setResourceName(originalFilename); // 显示给用户看的是原名
            resource.setResourceType(resourceType);
            resource.setFilePath(storedFileName); // 存的是UUID名
            resource.setCourse(course);

            resourceRepository.save(resource);

            return ResponseMessage.success("上传成功");
        } catch (IOException e) {
            return new ResponseMessage<>(500, "文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<List<Resource>> listResources(Integer courseId) {
        List<Resource> list = resourceRepository.findByCourseId(courseId);
        return ResponseMessage.success(list);
    }

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(Integer resourceId) {
        try {
            Resource resource = resourceRepository.findById(resourceId).orElse(null);
            if (resource == null) return ResponseEntity.notFound().build();

            Path filePath = this.fileStorageLocation.resolve(resource.getFilePath()).normalize();
            org.springframework.core.io.Resource fileResource = new UrlResource(filePath.toUri());

            if(fileResource.exists()) {
                // 解决中文文件名乱码问题 (简单处理)
                String encodedFileName = java.net.URLEncoder.encode(resource.getResourceName(), "UTF-8");

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                        .body(fileResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException | java.io.UnsupportedEncodingException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
