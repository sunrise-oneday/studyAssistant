package org.example.studyassistant.repository;

import org.example.studyassistant.pojo.entity.Course;
import org.example.studyassistant.pojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findByCourseCode(String courseCode);

    List<Course> findByTeacher(User teacher);
}
