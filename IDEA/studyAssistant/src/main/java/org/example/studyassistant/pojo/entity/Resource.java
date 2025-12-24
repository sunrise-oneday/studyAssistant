package org.example.studyassistant.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resources")
@Data
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String resourceName; // 文件名 (例如: Java基础.ppt)

    @Column(nullable = false)
    private String resourceType; // 类型 (PPT, VIDEO, DOCUMENTS,OTHER)

    @Column(nullable = false)
    private String filePath; // 磁盘存储路径

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;
}