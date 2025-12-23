package org.example.studyassistant.pojo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "courses")
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String courseCode; // 课程编号 (例如: CS2025)

    @Column(nullable = false)
    private String courseName; // 课程名称

    private String description; // 课程描述

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher; // 课程创建者 (关联 User 表)
}