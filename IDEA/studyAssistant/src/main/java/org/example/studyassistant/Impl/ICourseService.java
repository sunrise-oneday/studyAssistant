package org.example.studyassistant.Impl;

import jakarta.transaction.Transactional;
import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.example.studyassistant.pojo.entity.Course;

import java.util.List;
import java.util.Map;

public interface ICourseService {

    @Transactional
    ResponseMessage<?> joinCourse(String courseCode, String studentName);

    ResponseMessage<List<Map<String, Object>>> getMyCourses(String studentName);

    @Transactional
    ResponseMessage<?> createCourse(Course course, String teacherName);

    ResponseMessage<List<Course>> getCoursesByTeacher(String teacherName);

    ResponseMessage<Map<String, Object>> getCourseDetail(Integer courseId);

    @Transactional
    ResponseMessage<?> replyFeedback(Integer feedbackId, String response);

    @Transactional
    ResponseMessage<?> submitFeedback(Integer courseId, String studentName, String difficultyType, String description);

    ResponseMessage<java.util.List<java.util.Map<String, Object>>> getMyFeedbacks(Integer courseId, String studentName);
    // 在 CourseService 类中添加以下方法
    @Transactional
    ResponseMessage<?> updateStudentScore(Integer courseId, String studentName, Double score);
}
