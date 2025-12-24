import axios from 'axios';
import config from '@/api/config';

// 上传资源 (需要传 FormData)
export const uploadResource = (formData) => {
  return axios.post(config.host + 'resource/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

// 获取资源列表
export const getResourceList = (courseId) => {
  return axios.get(config.host + 'resource/list/' + courseId);
};

// 下载资源地址 (直接拼接URL)
export const getDownloadUrl = (resourceId) => {
  return config.host + 'resource/download/' + resourceId;
};
