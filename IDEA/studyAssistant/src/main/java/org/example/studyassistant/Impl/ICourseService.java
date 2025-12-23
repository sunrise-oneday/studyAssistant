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
}
