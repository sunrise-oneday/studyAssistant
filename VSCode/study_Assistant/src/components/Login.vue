<template>
  <div class="login-container">
    <div class="login-box">
      <h2>AI 校园学习助手 - 登录</h2>
      
      <form @submit.prevent="handleLogin">
        <!-- 删除了身份选择 Radio Group -->

        <!-- 账号输入 -->
        <div class="input-group">
          <input type="text" v-model="loginForm.name" placeholder="请输入用户名" required />
        </div>

        <!-- 密码输入 -->
        <div class="input-group">
          <input type="password" v-model="loginForm.password" placeholder="请输入密码" required />
        </div>

        <button type="submit" class="login-btn">登 录</button>
        <div class="link-area" style="margin-top: 15px; cursor: pointer; color: #409EFF;">
            <span @click="$router.push('/register')">没有账号？去注册</span>
        </div>
      </form>
      
      <p class="msg">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/sys/auth'; 
import config from '@/api/config';

export default {
  data() {
    return {
      loginForm: {
        name: '',     // 对应后端的 UserDTO.name
        password: ''  // 对应后端的 UserDTO.password
        // role 字段已移除，登录不需要传
      },
      message: ''
    };
  },
  methods: {
    async handleLogin() {
      this.message = '';
      try {
        const response = await login(this.loginForm);
        const res = response.data;

        if (res && res.code === 200) {
          this.message = '登录成功！';
          const data = res.data || {};
          const token = data.token || '';
          const role = data.role || '';
          const name = data.name || '';
          config.token.set(token);
          config.user.set({ name, role });
          config.tokenTime.set(Date.now());
          const target = role === 'TEACHER' ? { name: 'TeacherHome' } : { name: 'StudentHome' };
          await this.$router.push(target);
        } else {
          this.message = (res && res.message) || '登录失败';
        }
      } catch (error) {
        console.error(error);
        if (error.response && error.response.data) {
            this.message = error.response.data.message || '登录失败';
        } else {
            this.message = '网络连接失败';
        }
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.login-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  width: 350px;
  text-align: center;
}
.role-group {
  margin-bottom: 20px;
}
.role-group label {
  margin: 0 15px;
  cursor: pointer;
}
.input-group {
  margin-bottom: 15px;
}
.input-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}
.login-btn {
  width: 100%;
  padding: 10px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}
.login-btn:hover {
  background-color: #66b1ff;
}
.msg {
  margin-top: 10px;
  color: red;
  font-size: 14px;
}
</style>
