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
      console.log('🔐 开始登录流程...');
      console.log('📝 登录表单数据:', JSON.stringify(this.loginForm));
      
      try {
          console.log('🌐 发送登录请求到后端...');
          console.log('📤 请求地址:', '/api/user/login');
          console.log('📝 请求数据:', JSON.stringify(this.loginForm));
          
          const response = await login(this.loginForm);
          
          console.log('✅ 收到后端响应，状态码:', response.status);
          console.log('📦 响应头:', response.headers);
          console.log('🔍 响应数据详情:', JSON.stringify(response.data, null, 2));
          
          const res = response.data;
          console.log('📊 解析后的响应对象:', {
            code: res?.code,
            message: res?.message,
            data: res?.data
          });

          if (res && res.code === 200) {
            console.log('🎉 登录成功！状态码:', res.code, '消息:', res.message);
            this.message = '登录成功！';
            
            // 后端返回的数据结构：token、role、name直接在res.data中
            const data = res.data || res || {};
            console.log('📋 响应数据内容:', data);
            
            // 尝试从不同层级获取数据
            const token = data.token || '';
            const role = data.role || '';
            const name = data.name || '';
            
            console.log('🔑 获取到的Token:', token ? `已获取 (长度: ${token.length})` : '未获取');
            console.log('👤 用户信息:', { name, role });
            console.log('📋 数据完整性检查:', {
              tokenExists: !!token,
              roleExists: !!role,
              nameExists: !!name
            });
            
            // 立即保存配置
            config.token.set(token);
            config.user.set({ name, role });
            config.tokenTime.set(Date.now());
            
            console.log('📊 配置已保存到本地存储');
            console.log('💾 存储验证:', {
              token: config.token.get(),
              user: config.user.get(),
              tokenTime: config.tokenTime.get()
            });
            
            // 验证本地存储是否真的保存了
            const storedUser = localStorage.getItem('user');
            const storedToken = localStorage.getItem('token');
            console.log('🔍 本地存储验证 - user:', storedUser);
            console.log('🔍 本地存储验证 - token:', storedToken);
            
            // 立即执行路由跳转，不使用延迟
            console.log('🚀 立即执行路由跳转...');
            const target = role === 'TEACHER' ? { name: 'TeacherHome' } : { name: 'StudentHome' };
            console.log('🎯 目标路由:', target);
            console.log('👤 用户角色:', role);
            
            // 先尝试使用名称跳转
            this.$router.push(target).then(() => {
              console.log('✅ 路由跳转成功');
              console.log('📍 跳转后路由:', this.$route);
            }).catch(error => {
              console.error('❌ 路由跳转失败:', error);
              console.error('🔍 路由错误详情:', {
                name: error.name,
                message: error.message,
                stack: error.stack
              });
              
              // 如果名称跳转失败，尝试使用路径跳转
              const path = role === 'TEACHER' ? '/teacher/home' : '/student/home';
              console.log('🔄 尝试使用路径跳转:', path);
              this.$router.push(path).then(() => {
                console.log('✅ 使用路径跳转成功');
              }).catch(err => {
                console.error('❌ 路径跳转也失败:', err);
                console.error('💥 最终路由跳转失败，请检查路由配置');
                
                // 如果路径跳转也失败，尝试直接修改location
                console.log('🔄 尝试使用location跳转');
                window.location.href = path;
              });
            });
          } else {
            console.warn('⚠️ 登录失败，状态码:', res?.code, '消息:', res?.message);
            console.warn('🔍 失败响应详情:', res);
            this.message = (res && res.message) || '登录失败';
          }
        } catch (error) {
        console.error('❌ 登录过程中发生错误:', error);
        console.error('🔍 错误详情:', {
          name: error.name,
          message: error.message,
          stack: error.stack,
          response: error.response ? {
            status: error.response.status,
            statusText: error.response.statusText,
            data: error.response.data
          } : '无响应数据'
        });
        
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
