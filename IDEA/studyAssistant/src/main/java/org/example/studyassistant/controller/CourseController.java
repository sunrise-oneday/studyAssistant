package org.example.studyassistant.controller;

import jakarta.transaction.Transactional;
import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.example.studyassistant.pojo.entity.Course;
import org.example.studyassistant.repository.StudentCourseRepository;
import org.example.studyassistant.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    // 1. 学生加入课程
    @PostMapping("/join")
    public ResponseMessage<?> joinCourse(@RequestBody Map<String, String> params) {
        String courseCode = params.get("courseCode");
        String studentName = params.get("studentName");
        return courseService.joinCourse(courseCode, studentName);
    }

    // 2. 获取学生已加入的课程列表
    @PostMapping("/my-courses")
    public ResponseMessage<List<Map<String, Object>>> getMyCourses(@RequestBody Map<String, String> params) {
        String studentName = params.get("studentName");
        return courseService.getMyCourses(studentName);
    }

    // 3. 教师创建课程
    @PostMapping("/create")
    public ResponseMessage<?> createCourse(@RequestBody Map<String, Object> params) {
        Course course = new Course();
        // 自动生成编号逻辑建议放在 Service，或者这里生成
        String code = (String) params.get("courseCode");
        if(code == null || code.isEmpty()) {
            code = "CS-" + System.currentTimeMillis() / 1000; // 简单生成
        }
        course.setCourseCode(code);
        course.setCourseName((String) params.get("courseName"));
        course.setDescription((String) params.get("description")); // 接收描述

        String teacherName = (String) params.get("teacherName");
        return courseService.createCourse(course, teacherName);
    }

    // 4. 获取教师创建的课程列表
    @PostMapping("/teacher-courses")
    public ResponseMessage<List<Course>> getTeacherCourses(@RequestBody Map<String, String> params) {
        String teacherName = params.get("teacherName");
        return courseService.getCoursesByTeacher(teacherName);
    }

    // 5. 获取课程详情（含反馈和学生）
    @GetMapping("/detail/{courseId}")
    public ResponseMessage<Map<String, Object>> getCourseDetail(@PathVariable Integer courseId) {
        return courseService.getCourseDetail(courseId);
    }

    // 6. 教师回复反馈
    @PostMapping("/reply-feedback")
    public ResponseMessage<?> replyFeedback(@RequestBody Map<String, Object> params) {
        Integer feedbackId = (Integer) params.get("feedbackId");
        String response = (String) params.get("response");
        return courseService.replyFeedback(feedbackId, response);
    }
    // 7. 教师打分接口
    @PostMapping("/grade")
    public ResponseMessage<?> gradeStudent(@RequestBody Map<String, Object> params) {
        Integer courseId = (Integer) params.get("courseId");
        String studentName = (String) params.get("studentName");
        // 处理前端传来的可能是 Integer 或 Double 的情况
        Object scoreObj = params.get("score");
        Double score = Double.valueOf(scoreObj.toString());

        return courseService.updateStudentScore(courseId, studentName, score);
    }
}