<template>
  <div class="register-container">
    <div class="register-box">
      <h2>{{ schoolName }} - 账号注册</h2>
      
      <form @submit.prevent="handleRegister">
        <!-- 身份选择 -->
        <div class="role-group">
          <label><input type="radio" value="TEACHER" v-model="registerForm.role"> 教师</label>
          <label><input type="radio" value="STUDENT" v-model="registerForm.role"> 学生</label>
        </div>

        <!-- 输入框组 -->
        <div class="input-group">
          <input type="text" v-model="registerForm.name" placeholder="设置用户名" required />
        </div>

        <div class="input-group">
          <input type="password" v-model="registerForm.password" placeholder="设置密码" required />
        </div>

        <button type="submit" class="register-btn">立即注册</button>
        
        <div class="link-area">
          <span @click="goToLogin">已有账号？去登录</span>
        </div>
      </form>
      
      <p class="msg">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/sys/auth';
import config from '@/api/config';

export default {
  data() {
    return {
      schoolName: config.webInfo.schoolName,
      registerForm: {
        name: '',
        password: '',
        role: 'STUDENT' // 默认学生
      },
      message: ''
    };
  },
  methods: {
    async handleRegister() {
      this.message = '';
      // 简单校验
      if (this.registerForm.password.length < 6) {
        this.message = '密码长度不能少于6位';
        return;
      }

      try {
        const response = await register(this.registerForm);
        const res = response.data;

        if (res.code === 200) {
          alert('注册成功！请登录');
          this.goToLogin();
        } else {
          this.message = res.message;
        }
      } catch (error) {
        console.error(error);
        if (error.response && error.response.data) {
            this.message = error.response.data.message || '注册失败';
        } else {
            this.message = '网络连接失败';
        }
      }
    },
    goToLogin() {
      // 如果你配置了路由，使用 this.$router.push('/login')
      // 这里为了演示，假设你是在 App.vue 里通过 v-if 切换组件，或者手动修改 URL
      // 简单起见，我们抛出一个事件让父组件处理，或者直接跳转路由
      if (this.$router) {
          this.$router.push('/login');
      } else {
          // 如果没有路由，临时用 alert 提示
          alert("请切换回登录页面进行登录");
      }
    }
  }
};
</script>

<style scoped>
/* 复用登录页的样式，稍微修改 */
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.register-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  width: 350px;
  text-align: center;
}
.role-group { margin-bottom: 20px; }
.role-group label { margin: 0 15px; cursor: pointer; }
.input-group { margin-bottom: 15px; }
.input-group input {
  width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box;
}
.register-btn {
  width: 100%; padding: 10px; background-color: #42b983; /* 绿色代表注册 */
  color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px;
}
.register-btn:hover { background-color: #3aa876; }
.msg { margin-top: 10px; color: red; font-size: 14px; }
.link-area { margin-top: 15px; font-size: 14px; color: #409EFF; cursor: pointer; }
</style>