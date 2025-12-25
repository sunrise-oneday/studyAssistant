import axios from 'axios';
import config from '@/api/config';

// 获取我的课程列表
export const getMyCourses = (params) => {
  return axios.post(config.host + 'course/my-courses', params);
};

// 加入课程
export const joinCourse = (params) => {
  return axios.post(config.host + 'course/join', params);
};
// 创建课程
export const createCourse = (params) => {
  return axios.post(config.host + 'course/create', params);
};

// 获取教师课程列表
export const getTeacherCourses = (params) => {
  return axios.post(config.host + 'course/teacher-courses', params);
};

// 获取课程详情（含反馈）
export const getCourseDetail = (courseId) => {
  return axios.get(config.host + 'course/detail/' + courseId);
};

// 回复反馈
export const replyFeedback = (params) => {
 return axios.post(config.host + 'course/reply-feedback', params);
};

// 学生提交反馈
export const submitFeedback = (params) => {
    return axios.post(config.host + 'course/submit-feedback', params);
};

// 获取我的反馈列表
export const getMyFeedbacks = (params) => {
    return axios.post(config.host + 'course/my-feedbacks', params);
};

// 提交反馈
export const submitFeedback = (params) => {
    return axios.post(config.host + 'course/submit-feedback', params);
};

// 获取我的反馈列表
export const getMyFeedbacks = (params) => {
    return axios.post(config.host + 'course/my-feedbacks', params);
};

// 教师打分
export const gradeStudent = (params) => {
  return axios.post(config.host + 'course/grade', params);
};
