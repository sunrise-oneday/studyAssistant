<template>
  <div class="home-container">
    <!-- 1. 顶部导航栏 -->
    <header class="navbar">
      <div class="nav-content">
        <div class="logo">AI 校园学习助手</div>
        <div class="nav-items">
          <span class="nav-link active">首页</span>
          <span class="nav-link">AI 助手</span>
          
          <div class="user-profile">
            <span class="user-name">{{ user.name }}</span>
            <span class="role-badge">学生</span>
          </div>
          
          <button class="logout-btn" @click="handleLogout">退出</button>
        </div>
      </div>
    </header>

    <div class="main-wrapper">
      <!-- 2. 上半部分：个人信息 + 课程录入 -->
      <div class="top-section">
        
        <!-- 左侧：个人信息卡片 -->
        <div class="info-card card-shadow">
          <div class="avatar-wrapper">
            <!-- 使用随机头像API，或者替换为你本地的assets图片 -->
            <img 
              src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" 
              alt="用户头像" 
              class="avatar-img"
            />
          </div>
          <div class="user-info">
            <h3>{{ user.name || '同学' }}</h3>
            <p>欢迎回来，开始今天的学习吧！</p>
          </div>
        </div>

        <!-- 右侧：加入课程卡片 -->
        <div class="join-card card-shadow">
          <div class="join-content">
            <h3>加入新课程</h3>
            <p class="sub-text">请输入教师提供的课程编号加入班级</p>
            
            <div class="form-row">
              <input 
                type="text" 
                v-model="courseCode" 
                class="custom-input" 
                placeholder="例如: CS-2025-01"
              />
              <button 
                class="primary-btn" 
                @click="handleJoinCourse" 
                :disabled="loading"
              >
                {{ loading ? '加入中...' : '加入课程' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 3. 分隔栏 / 标题栏 -->
      <div class="section-divider">
        <div class="divider-line"></div>
        <span class="divider-title">已加入课程</span>
        <div class="divider-line"></div>
      </div>

      <!-- 4. 下半部分：课程列表 (Grid布局) -->
      <div class="course-list-section">
        <div class="course-grid">
          <!-- 循环渲染课程 -->
          <div 
            v-for="course in courseList" 
            :key="course.id" 
            class="course-item card-shadow"
          >
            <div class="course-cover">
              <!-- 生成基于课程名的首字母或图标 -->
              <span>{{ course.courseName ? course.courseName.charAt(0) : '课' }}</span>
            </div>
            <div class="course-details">
              <h4>{{ course.courseName }}</h4>
              <p class="code">编号: {{ course.courseCode }}</p>
              <p class="teacher">讲师: {{ course.teacherName || '未知' }}</p>
            </div>
          </div>

          <!-- 无数据时的占位符 -->
          <div v-if="courseList.length === 0" class="empty-state">
            <p>暂未加入任何课程</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import config from '@/api/config';
import { logout } from '@/api/sys/auth';
// 引入下面定义的课程API
import { getMyCourses, joinCourse } from '@/api/sys/course';

export default {
  name: 'StudentHome',
  data() {
    return {
      user: {
        name: '',
        role: ''
      },
      courseCode: '',
      courseList: [], // 课程列表数据
      loading: false
    };
  },
  created() {
    // 1. 初始化用户信息
    const localUser = config.user.get();
    if (localUser) {
      this.user = localUser;
    } else {
      // 如果没有用户信息，强制跳回登录
      this.$router.push('/login');
    }

    // 2. 获取已加入的课程列表
    this.fetchCourseList();
  },
  methods: {
    // 获取课程列表
    async fetchCourseList() {
      try {
        // 假设后端需要传用户名或者ID，这里传name
        const res = await getMyCourses({ studentName: this.user.name });
        if (res.data.code === 200) {
          this.courseList = res.data.data;
        }
      } catch (error) {
        console.error("获取课程列表失败", error);
       
      }
    },

    // 加入课程
    async handleJoinCourse() {
      if (!this.courseCode.trim()) {
        alert("请输入课程编号");
        return;
      }
      
      this.loading = true;
      try {
        const params = {
          courseCode: this.courseCode,
          studentName: this.user.name
        };
        const res = await joinCourse(params);
        
        if (res.data.code === 200) {
          alert("加入成功！");
          this.courseCode = ''; // 清空输入框
          this.fetchCourseList(); // 刷新列表
        } else {
          alert(res.data.message || "加入失败");
        }
      } catch (error) {
        console.error(error);
        alert("系统错误或网络异常");
      } finally {
        this.loading = false;
      }
    },

    // 退出登录
    handleLogout() {
      if(confirm('确定要退出登录吗？')) {
        logout();
        this.$router.push('/login');
      }
    }
  }
};
</script>

<style scoped>
/* 全局容器：沿用登录页的灰背景色，保持清爽 */
.home-container {
  min-height: 100vh;
  background-color: #f0f2f5; 
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* --- 1. 导航栏样式 --- */
.navbar {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  height: 64px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.logo {
  font-size: 22px;
  font-weight: 600;
  color: #409EFF; /* 品牌蓝 */
}

.nav-items {
  display: flex;
  align-items: center;
  gap: 25px;
}

.nav-link {
  color: #606266;
  cursor: pointer;
  font-size: 16px;
  transition: color 0.3s;
}
.nav-link:hover, .nav-link.active {
  color: #409EFF;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #333;
}
.role-badge {
  background-color: #ecf5ff;
  color: #409EFF;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  border: 1px solid #d9ecff;
}

.logout-btn {
  padding: 6px 16px;
  border: 1px solid #ff4d4f;
  color: #ff4d4f;
  background: transparent;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}
.logout-btn:hover {
  background: #ff4d4f;
  color: white;
}

/* --- 主内容区域限制宽度 --- */
.main-wrapper {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

/* --- 通用卡片样式 (白色、阴影、圆角) --- */
.card-shadow {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

/* --- 2. 上半部分 (Flex布局：左30%，右70%) --- */
.top-section {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  height: 180px; /* 固定高度保持整齐 */
}

/* 左侧：个人信息 */
.info-card {
  flex: 3; /* 对应图示约 30% */
  display: flex;
  align-items: center;
  padding: 20px;
  gap: 20px;
}
.avatar-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #e6e6e6;
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.user-info h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #303133;
}
.user-info p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* 右侧：加入课程 */
.join-card {
  flex: 7; /* 对应图示约 70% */
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 40px;
}
.join-content h3 {
  margin: 0 0 5px 0;
  color: #303133;
}
.sub-text {
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}
.form-row {
  display: flex;
  gap: 15px;
  max-width: 500px;
}
/* 统一输入框样式 */
.custom-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  outline: none;
  transition: border-color 0.3s;
}
.custom-input:focus {
  border-color: #409EFF;
}
/* 统一按钮样式 */
.primary-btn {
  background-color: #409EFF;
  color: white;
  border: none;
  padding: 10px 25px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 15px;
  transition: background-color 0.3s;
}
.primary-btn:hover {
  background-color: #66b1ff;
}
.primary-btn:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

/* --- 3. 分隔标题 --- */
.section-divider {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.divider-line {
  flex: 1;
  height: 1px;
  background-color: #e4e7ed;
}
.divider-title {
  padding: 0 20px;
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

/* --- 4. 课程列表 Grid布局 --- */
.course-list-section {
  min-height: 300px;
}

.course-grid {
  display: grid;
  /* 核心需求：一行4个，自动换行 */
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.course-item {
  background: white;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  cursor: pointer;
}
.course-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.1);
}

.course-cover {
  width: 60px;
  height: 60px;
  background-color: #ecf5ff; /* 浅蓝背景 */
  color: #409EFF;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
}

.course-details h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #303133;
  /* 限制标题行数 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}
.course-details p {
  margin: 4px 0;
  font-size: 13px;
  color: #909399;
}

/* 空状态样式 */
.empty-state {
  grid-column: 1 / -1; /* 跨越所有列 */
  text-align: center;
  padding: 50px;
  color: #909399;
}
</style>