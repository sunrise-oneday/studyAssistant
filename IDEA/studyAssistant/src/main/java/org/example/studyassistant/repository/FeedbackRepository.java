package org.example.studyassistant.repository;

import org.example.studyassistant.pojo.entity.Feedback;
import org.example.studyassistant.pojo.entity.Course;
import org.example.studyassistant.pojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Integer> {
    java.util.List<Feedback> findByCourseAndStudent(Course course, User student);
}
