import axios from 'axios';
import config from '@/api/config';

// 加入课程
export const joinCourse = (params) => {
    return axios.post(config.host + 'course/join', params);
};

// 获取我的课程列表
export const getMyCourses = (name) => {
    return axios.get(config.host + 'course/my-courses', {
        params: { name: name }
    });
};