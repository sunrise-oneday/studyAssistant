package org.example.studyassistant.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.example.studyassistant.pojo.enums.DifficultyType;

@Entity
@Table
@Data
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyType difficultyType;

    @Column(nullable = false)
    private String description;

    private String teacherResponse;
    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore // 防止JSON死循环
    private  Course course;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private  User student;




}
