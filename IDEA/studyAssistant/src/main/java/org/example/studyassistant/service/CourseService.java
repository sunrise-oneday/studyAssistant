package org.example.studyassistant.service;

import jakarta.transaction.Transactional;
import org.example.studyassistant.Impl.ICourseService;
import org.example.studyassistant.pojo.dto.ResponseMessage;
import org.example.studyassistant.pojo.entity.Course;
import org.example.studyassistant.pojo.entity.Feedback;
import org.example.studyassistant.pojo.entity.StudentCourse;
import org.example.studyassistant.pojo.entity.User;
import org.example.studyassistant.pojo.enums.DifficultyType;
import org.example.studyassistant.repository.CourseRepository;
import org.example.studyassistant.repository.FeedbackRepository;
import org.example.studyassistant.repository.StudentCourseRepository;
import org.example.studyassistant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class CourseService implements ICourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

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

    @Override
    public ResponseMessage<Map<String, Object>> getCourseDetail(Integer courseId) {
        // 1. 获取课程信息
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return new ResponseMessage<>(404, "课程不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("course", course);

        // 2. 获取反馈列表 (利用 JPA 的关联查询，或者直接查 FeedbackRepository)
        result.put("feedbacks", course.getFeedbacks());

        // 3. 获取学生名单和成绩
        List<StudentCourse> scList = studentCourseRepository.findByCourse(course);
        List<Map<String, Object>> studentList = new ArrayList<>();
        for (StudentCourse sc : scList) {
            Map<String, Object> sMap = new HashMap<>();
            sMap.put("name", sc.getStudent().getName()); // 学生姓名
            sMap.put("score", sc.getScore());           // 成绩
            studentList.add(sMap);
        }
        result.put("students", studentList);

        return ResponseMessage.success(result);
    }

    @Transactional
    @Override
    public ResponseMessage<?> replyFeedback(Integer feedbackId, String response) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElse(null);
        if (feedback == null) {
            return new ResponseMessage<>(404, "反馈不存在");
        }

        // 更新教师回答
        feedback.setTeacherResponse(response);
        feedbackRepository.save(feedback);

        return ResponseMessage.success("回复成功");
    }

@Transactional
    @Override
    public ResponseMessage<?> submitFeedback(Integer courseId, String studentName, String difficultyType, String description) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return new ResponseMessage<>(404, "课程不存在");
        }
        User student = userRepository.findByName(studentName);
        if (student == null) {
            return new ResponseMessage<>(404, "学生不存在");
        }
        DifficultyType type;
        try {
            type = DifficultyType.valueOf(difficultyType);
        } catch (Exception e) {
            return new ResponseMessage<>(400, "难点类型错误");
        }
        Feedback fb = new Feedback();
        fb.setCourse(course);
        fb.setStudent(student);
        fb.setDifficultyType(type);
        fb.setDescription(description);
        feedbackRepository.save(fb);
        return ResponseMessage.success("提交成功");
    }

    @Override
    public ResponseMessage<List<Map<String, Object>>> getMyFeedbacks(Integer courseId, String studentName) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return new ResponseMessage<>(404, "课程不存在");
        }
        User student = userRepository.findByName(studentName);
        if (student == null) {
            return new ResponseMessage<>(404, "学生不存在");
        }
        List<Feedback> list = feedbackRepository.findByCourseAndStudent(course, student);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Feedback f : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", f.getId());
            m.put("difficultyType", f.getDifficultyType().name());
            m.put("description", f.getDescription());
            m.put("teacherResponse", f.getTeacherResponse());
            result.add(m);
        }
        return ResponseMessage.success(result);
    }
  
  // 在 CourseService 类中添加以下方法
    @Transactional
    @Override
    public ResponseMessage<?> updateStudentScore(Integer courseId, String studentName, Double score) {
        // 查找课程
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return new ResponseMessage<>(404, "课程不存在");
        }

        // 查找学生
        User student = userRepository.findByName(studentName);
        if (student == null) {
            return new ResponseMessage<>(404, "学生不存在");
        }

        // 查找学生课程关联记录
        StudentCourse studentCourse = studentCourseRepository.findByStudentAndCourse(student, course);
        if (studentCourse == null) {
            return new ResponseMessage<>(404, "该学生未加入此课程");
        }

        // 更新成绩
        studentCourse.setScore(score);
        studentCourseRepository.save(studentCourse);

        return ResponseMessage.success("成绩更新成功");
    }
}
