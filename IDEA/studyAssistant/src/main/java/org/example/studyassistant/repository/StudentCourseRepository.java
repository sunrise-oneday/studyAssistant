// StudentCourseRepository.java
package org.example.studyassistant.repository;
import org.example.studyassistant.pojo.entity.StudentCourse;
import org.example.studyassistant.pojo.entity.User;
import org.example.studyassistant.pojo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Integer> {
    // 查询某学生是否已加入某课程
    StudentCourse findByStudentAndCourse(User student, Course course);

    // 查询某学生的所有选课记录
    List<StudentCourse> findByStudent(User student);
}