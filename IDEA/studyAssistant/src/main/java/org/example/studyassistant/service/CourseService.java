package org.example.studyassistant.service;

import jakarta.transaction.Transactional;
import org.example.studyassistant.Impl.ICourseService;
import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.example.studyassistant.pojo.entity.Course;
import org.example.studyassistant.pojo.entity.StudentCourse;
import org.example.studyassistant.pojo.entity.User;
import org.example.studyassistant.repository.CourseRepository;
import org.example.studyassistant.repository.StudentCourseRepository;
import org.example.studyassistant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseService implements ICourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public ResponseMessage<?> joinCourse(String courseCode, String studentName) {
        // 查找课程
        Course course = courseRepository.findByCourseCode(courseCode);
        if (course == null) {
            return new ResponseMessage<>(404, "课程编号不存在");
        }

        // 查找学生
        User student = userRepository.findByName(studentName);
        if (student == null) {
            return new ResponseMessage<>(404, "学生信息错误");
        }

        // 检查是否已加入
        StudentCourse exist = studentCourseRepository.findByStudentAndCourse(student, course);
        if (exist != null) {
            return new ResponseMessage<>(400, "你已经加入过该课程了");
        }

        // 创建关联
        StudentCourse sc = new StudentCourse();
        sc.setCourse(course);
        sc.setStudent(student);
        studentCourseRepository.save(sc);

        return ResponseMessage.success();
    }

    @Override
    public ResponseMessage<List<Map<String, Object>>> getMyCourses(String studentName) {
        User student = userRepository.findByName(studentName);
        if (student == null) {
            return new ResponseMessage<>(404, "学生不存在");
        }

        List<StudentCourse> list = studentCourseRepository.findByStudent(student);

        // 转换为前端需要的简单格式
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (StudentCourse sc : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sc.getCourse().getId());
            map.put("courseName", sc.getCourse().getCourseName());
            map.put("courseCode", sc.getCourse().getCourseCode());
            map.put("teacherName", sc.getCourse().getTeacher().getName());
            map.put("score", sc.getScore()); // 成绩
            resultList.add(map);
        }

        return ResponseMessage.success(resultList);
    }

    @Transactional
    @Override
    public ResponseMessage<?> createCourse(Course course, String teacherName) {
        User teacher = userRepository.findByName(teacherName);
        if (teacher == null) {
            return new ResponseMessage<>(404, "教师不存在");
        }

        // 检查课程编号是否已存在
        Course existingCourse = courseRepository.findByCourseCode(course.getCourseCode());
        if (existingCourse != null) {
            return new ResponseMessage<>(400, "课程编号已存在");
        }

        course.setTeacher(teacher);
        courseRepository.save(course);

        return ResponseMessage.success("课程创建成功");
    }

    @Override
    public ResponseMessage<List<Course>> getCoursesByTeacher(String teacherName) {
        User teacher = userRepository.findByName(teacherName);
        if (teacher == null) {
            return new ResponseMessage<>(404, "教师不存在");
        }

        List<Course> courses = courseRepository.findByTeacher(teacher);
        return ResponseMessage.success(courses);
    }


}
