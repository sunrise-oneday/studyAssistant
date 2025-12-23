package org.example.studyassistant.repository;

import org.example.studyassistant.pojo.entity.Course;
import org.example.studyassistant.pojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findByCourseCode(String courseCode);

    List<Course> findByTeacher(User teacher);
}
