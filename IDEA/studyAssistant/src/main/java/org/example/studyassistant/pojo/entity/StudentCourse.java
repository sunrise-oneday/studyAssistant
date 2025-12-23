package org.example.studyassistant.pojo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "student_courses")
@Data
public class StudentCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // 关联课程

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student; // 关联学生

    private Double score; // 成绩 (初始为 null)
}