// src/api/config.js

const config = {
  // 后端接口基础地址 - 使用相对路径，通过Vite代理转发
  // Vite开发服务器会代理 /api 请求到 http://localhost:8080/api/
  host: '/api/',

  // 静态资源基础地址 (如果有图片上传等功能会用到)
  hostfile: 'http://localhost:8080/',

  // 网站基础信息
  webInfo: {
    department: '教务处', // 或者 '计算机学院'
    schoolName: 'AI 校园学习助手',
  },

  // 用户信息管理
  user: {
    // 初始化时尝试从本地读取
    data: (() => {
      try {
        return (
          JSON.parse(sessionStorage.getItem('user')) || { name: '', role: '' }
        );
      } catch (e) {
        return { name: '', role: '' };
      }
    })(),
    // 获取用户信息对象
    get: function () {
      try {
        return JSON.parse(sessionStorage.getItem('user'));
      } catch (e) {
        return null;
      }
    },
    // 设置用户信息 (登录成功后调用)
    set: function (user) {
      sessionStorage.setItem('user', JSON.stringify(user));
      this.data = user;
    },
    // 清除用户信息 (退出登录时调用)
    clear: function () {
      sessionStorage.removeItem('user');
      this.data = { name: '', role: '' }; // 重置内存数据
    },
  },

  // Token 管理
  token: {
    data: sessionStorage.getItem('token'),
    set: function (token) {
      sessionStorage.setItem('token', token);
      this.data = token;
    },
    get: function () {
      return sessionStorage.getItem('token');
    },
    clear: function () {
      sessionStorage.removeItem('token');
      this.data = null;
    },
  },

  // Token 过期时间管理 (可选，配合后端过期时间)
  tokenTime: {
    data: sessionStorage.getItem('tokenTime'),
    set: function (tokenTime) {
      sessionStorage.setItem('tokenTime', tokenTime);
    },
    get: function () {
      return sessionStorage.getItem('tokenTime');
    },
    clear: function () {
      sessionStorage.removeItem('tokenTime');
    },
  },
};

export default config;
