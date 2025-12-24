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
              <span>{{
                course.courseName ? course.courseName.charAt(0) : '课'
              }}</span>
            </div>
            <div class="course-details">
              <h4>{{ course.courseName }}</h4>
              <p class="code">编号: {{ course.courseCode }}</p>
              <p class="teacher">讲师: {{ course.teacherName || '未知' }}</p>

              <!-- 新增：查看资源按钮 -->
              <!-- @click.stop 防止冒泡，如果未来整个卡片可点击跳转详情页，这很有用 -->
              <button
                class="view-res-btn"
                @click.stop="openResourceModal(course)"
              >
                查看资源
              </button>
            </div>
          </div>

          <!-- 无数据时的占位符 -->
          <div v-if="courseList.length === 0" class="empty-state">
            <p>暂未加入任何课程</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 5. 资源列表弹窗 (新增部分) -->
    <div
      v-if="showResourceModal"
      class="modal-overlay"
      @click.self="closeModal"
    >
      <div class="modal-content card-shadow">
        <div class="modal-header">
          <h3>{{ currentCourseName }} - 课程资源</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div
            v-if="currentCourseResources.length === 0"
            class="empty-res-state"
          >
            <p>📚 老师暂时还没有上传资源哦~</p>
          </div>
          <ul class="res-list" v-else>
            <li
              v-for="res in currentCourseResources"
              :key="res.id"
              class="res-row"
            >
              <span class="res-icon">{{
                res.resourceType === 'VIDEO'
                  ? '🎥'
                  : res.resourceType === 'DOCUMENT'
                    ? '📚'
                    : '📄'
              }}</span>
              <div class="res-info-text">
                <span class="res-name">{{ res.resourceName }}</span>
                <span class="res-tag">{{ res.resourceType }}</span>
              </div>
              <button class="download-link" @click="downloadResource(res.id)">
                下载
              </button>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import config from '@/api/config';
import { logout } from '@/api/sys/auth';
import { getMyCourses, joinCourse } from '@/api/sys/course';
// 引入资源相关的API
import { getResourceList, getDownloadUrl } from '@/api/sys/resource';

export default {
  name: 'StudentHome',
  data() {
    return {
      user: {
        name: '',
        role: '',
      },
      courseCode: '',
      courseList: [],
      loading: false,

      // 弹窗相关数据
      showResourceModal: false,
      currentCourseResources: [],
      currentCourseName: '',
    };
  },
  created() {
    const localUser = config.user.get();
    if (localUser) {
      this.user = localUser;
    } else {
      this.$router.push('/login');
    }
    this.fetchCourseList();
  },
  methods: {
    // 获取课程列表
    async fetchCourseList() {
      try {
        const res = await getMyCourses({ studentName: this.user.name });
        if (res.data.code === 200) {
          this.courseList = res.data.data;
        }
      } catch (error) {
        console.error('获取课程列表失败', error);
      }
    },

    // 加入课程
    async handleJoinCourse() {
      if (!this.courseCode.trim()) {
        alert('请输入课程编号');
        return;
      }

      this.loading = true;
      try {
        const params = {
          courseCode: this.courseCode,
          studentName: this.user.name,
        };
        const res = await joinCourse(params);

        if (res.data.code === 200) {
          alert('加入成功！');
          this.courseCode = '';
          this.fetchCourseList();
        } else {
          alert(res.data.message || '加入失败');
        }
      } catch (error) {
        console.error(error);
        alert('系统错误或网络异常');
      } finally {
        this.loading = false;
      }
    },

    // 打开资源弹窗
    async openResourceModal(course) {
      this.currentCourseName = course.courseName;
      this.showResourceModal = true;
      this.currentCourseResources = []; // 打开前清空旧数据，避免显示上一个课程的资源

      try {
        const res = await getResourceList(course.id);
        if (res.data.code === 200) {
          this.currentCourseResources = res.data.data;
        }
      } catch (error) {
        console.error('获取资源失败', error);
      }
    },

    // 关闭弹窗
    closeModal() {
      this.showResourceModal = false;
    },

    // 下载资源
    downloadResource(id) {
      // 打开新窗口触发浏览器下载行为
      window.open(getDownloadUrl(id));
    },

    // 退出登录
    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        logout();
        this.$router.push('/login');
      }
    },
  },
};
</script>

<style scoped>
/* --- 基础布局 --- */
.home-container {
  min-height: 100vh;
  background-color: #f0f2f5;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue',
    Arial, sans-serif;
}

/* --- 1. 导航栏 --- */
.navbar {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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
  color: #409eff;
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
.nav-link:hover,
.nav-link.active {
  color: #409eff;
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
  color: #409eff;
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

/* --- 主内容区 --- */
.main-wrapper {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}
.card-shadow {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition:
    transform 0.3s,
    box-shadow 0.3s;
}

/* --- 2. 顶部区域 --- */
.top-section {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  height: 180px;
}
.info-card {
  flex: 3;
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

.join-card {
  flex: 7;
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
.custom-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  outline: none;
  transition: border-color 0.3s;
}
.custom-input:focus {
  border-color: #409eff;
}
.primary-btn {
  background-color: #409eff;
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

/* --- 3. 分隔线 --- */
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
  color: #409eff;
}

/* --- 4. 课程列表 --- */
.course-list-section {
  min-height: 300px;
}
.course-grid {
  display: grid;
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
  cursor: default; /* 改为 default 因为卡片本身不跳 */
}
.course-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}
.course-cover {
  width: 60px;
  height: 60px;
  background-color: #ecf5ff;
  color: #409eff;
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
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 50px;
  color: #909399;
}

/* --- 新增：查看资源按钮样式 --- */
.view-res-btn {
  margin-top: 15px;
  background: #ecf5ff;
  color: #409eff;
  border: 1px solid #b3d8ff;
  padding: 6px 16px;
  border-radius: 15px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}
.view-res-btn:hover {
  background: #409eff;
  color: white;
  border-color: #409eff;
}

/* --- 新增：弹窗样式 --- */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5); /* 半透明遮罩 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
  backdrop-filter: blur(2px); /* 磨砂玻璃效果 */
}
.modal-content {
  width: 500px;
  background: white;
  padding: 25px;
  border-radius: 12px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  display: flex;
  flex-direction: column;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 15px;
  margin-bottom: 15px;
}
.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}
.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: #999;
  line-height: 1;
}
.close-btn:hover {
  color: #666;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
}
.empty-res-state {
  text-align: center;
  color: #999;
  padding: 30px;
  font-size: 14px;
}

/* 资源列表 */
.res-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.res-row {
  display: flex;
  align-items: center;
  padding: 12px 10px;
  border-bottom: 1px dashed #eee;
  transition: background-color 0.2s;
}
.res-row:hover {
  background-color: #f9f9f9;
}
.res-row:last-child {
  border-bottom: none;
}
.res-icon {
  font-size: 22px;
  margin-right: 15px;
}
.res-info-text {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.res-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.res-tag {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}
.download-link {
  color: #409eff;
  background: none;
  border: 1px solid transparent;
  cursor: pointer;
  font-size: 13px;
  padding: 4px 10px;
  border-radius: 4px;
}
.download-link:hover {
  background-color: #ecf5ff;
  font-weight: 500;
}
</style>
