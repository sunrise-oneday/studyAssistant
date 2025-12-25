<template>
  <div class="home-container">
    <!-- 1. 顶部导航栏 -->
    <header class="navbar">
      <div class="nav-content">
        <div class="logo">AI 校园学习助手</div>
        <div class="nav-items">
          <span
            class="nav-link"
            :class="{ active: !currentCourse }"
            @click="backToDashboard"
            >首页</span
          >
          <span class="nav-link" @click="$router.push('/ai')">AI 助手</span>

          <div class="user-profile">
            <span class="user-name">{{ user.name }}</span>
            <span class="role-badge">学生</span>
          </div>

          <button class="logout-btn" @click="handleLogout">退出</button>
        </div>
      </div>
    </header>

    <div class="main-wrapper">
      <!-- 视图 1: 仪表盘 (Dashboard) -->
      <div v-if="!currentCourse" class="dashboard-view">
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
            <div
              v-for="course in courseList"
              :key="course.id"
              class="course-item card-shadow"
              @click="openCourseDetail(course)"
            >
              <div class="course-cover">
                <span>{{
                  course.courseName ? course.courseName.charAt(0) : '课'
                }}</span>
              </div>
              <div class="course-details">
                <h4>{{ course.courseName }}</h4>
                <p class="code">编号: {{ course.courseCode }}</p>
                <p class="teacher">讲师: {{ course.teacherName || '未知' }}</p>

                <button class="view-res-btn">进入课程</button>
              </div>
            </div>

            <div v-if="courseList.length === 0" class="empty-state">
              <p>暂未加入任何课程</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 视图 2: 课程详情面板 (Course Detail) -->
      <div v-else class="course-detail-view">
        <div class="detail-panel card-shadow">
          <button class="back-btn" @click="backToDashboard">← 返回列表</button>

          <div class="panel-body flex-row">
            <div class="course-img-placeholder">
              <div class="img-box">
                <svg class="course-svg" viewBox="0 0 120 90">
                  <rect
                    x="10"
                    y="12"
                    width="40"
                    height="60"
                    rx="6"
                    fill="#b3d8ff"
                  ></rect>
                  <rect
                    x="50"
                    y="12"
                    width="40"
                    height="60"
                    rx="6"
                    fill="#409eff"
                  ></rect>
                  <line
                    x1="10"
                    y1="36"
                    x2="90"
                    y2="36"
                    stroke="#ffffff"
                    stroke-width="4"
                  ></line>
                  <rect
                    x="25"
                    y="74"
                    width="70"
                    height="8"
                    rx="4"
                    fill="#c0c4cc"
                  ></rect>
                </svg>
              </div>
            </div>
            <div class="course-info-text">
              <h2>{{ currentCourse.courseName }}</h2>
              <div class="info-row">
                <span class="label">授课老师:</span>
                {{ currentCourse.teacherName || '未知' }}
              </div>
              <div class="info-row">
                <span class="label">当前成绩:</span>
                <span class="score">{{ currentCourse.score || '暂无' }}</span>
              </div>
              <div class="desc-box">
                <p class="label">课程描述:</p>
                <p class="desc-content">
                  {{
                    currentCourse.description ||
                    '这是一门非常有趣的课程，涵盖了核心知识点...'
                  }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- 面板 2: 难点反馈 -->
        <div class="feedback-panel card-shadow">
          <div class="panel-header">
            <h3>难点反馈</h3>
          </div>
          <div class="panel-body flex-row split-layout">
            <div class="feedback-form-section">
              <h4>发布新反馈</h4>
              <div class="form-group">
                <label>难点类型</label>
                <select v-model="feedbackForm.type" class="custom-select">
                  <option value="" disabled>请选择类型</option>
                  <option value="CONCEPT">概念模糊</option>
                  <option value="CALCULATION">计算困难</option>
                  <option value="METHOD">方法不当</option>
                </select>
              </div>
              <div class="form-group">
                <label>描述</label>
                <textarea
                  v-model="feedbackForm.content"
                  class="custom-textarea"
                  rows="4"
                  placeholder="请详细描述你遇到的困难..."
                ></textarea>
              </div>
              <button class="primary-btn" @click="submitFeedback">
                发布反馈
              </button>
            </div>

            <div class="feedback-list-section">
              <h4>我的反馈记录</h4>
              <div class="feedback-list">
                <div
                  v-for="item in feedbackList"
                  :key="item.id"
                  class="feedback-item"
                  :class="{ 'has-reply': item.reply }"
                  @click="viewReply(item)"
                >
                  <div class="fb-header">
                    <span class="fb-type-tag">{{
                      getTypeName(item.type)
                    }}</span>
                    <span class="fb-time">{{ item.time }}</span>
                  </div>
                  <p class="fb-content">{{ item.content }}</p>
                  <div class="fb-status">
                    <span v-if="item.reply" class="status-replied"
                      >✅ 老师已回复</span
                    >
                    <span v-else class="status-pending">⏳ 等待回复</span>
                  </div>
                </div>

                <div v-if="feedbackList.length === 0" class="empty-text">
                  暂无反馈记录
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 面板 3: 课程资源 -->
        <div class="resource-panel card-shadow">
          <div class="panel-header">
            <h3>课程资源</h3>
          </div>
          <div class="panel-body">
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
                      ? '📄'
                      : '📚'
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

    <!-- 弹窗：查看老师回复 (简单实现) -->
    <div
      v-if="showReplyModal"
      class="modal-overlay"
      @click.self="showReplyModal = false"
    >
      <div class="modal-content card-shadow" style="width: 400px">
        <div class="modal-header">
          <h3>老师回复</h3>
          <button class="close-btn" @click="showReplyModal = false">×</button>
        </div>
        <div class="modal-body">
          <p style="white-space: pre-wrap">{{ currentReply }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import config from '@/api/config';
import { logout } from '@/api/sys/auth';
import {
  getMyCourses,
  joinCourse,
  getCourseDetail,
  submitFeedback as apiSubmitFeedback,
  getMyFeedbacks,
} from '@/api/sys/course';
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
      currentCourse: null,
      currentCourseResources: [],
      feedbackForm: {
        type: '',
        content: '',
      },
      feedbackList: [],
      showReplyModal: false,
      currentReply: '',
    };
  },
  created() {
    console.log('🎯 StudentHome组件被创建');
    const localUser = config.user.get();
    console.log('🔍 从本地存储获取的用户信息:', localUser);
    
    if (localUser) {
      this.user = localUser;
      console.log('✅ 用户信息已设置:', this.user);
    } else {
      console.warn('⚠️ 用户信息不存在，重定向到登录页面');
      this.$router.push('/login');
      return;
    }
    
    console.log('📋 开始获取课程列表...');
    this.fetchCourseList();
  },
  methods: {
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
    async openCourseDetail(course) {
      this.currentCourse = { ...course };
      this.fetchResources(course.id);
      try {
        const res = await getCourseDetail(course.id);
        if (res.data.code === 200) {
          const detail = res.data.data || {};
          const c = detail.course || {};
          this.currentCourse.description = c.description || '';
          if (!this.currentCourse.teacherName && c.teacher && c.teacher.name) {
            this.currentCourse.teacherName = c.teacher.name;
          }
          const list = Array.isArray(detail.feedbacks) ? detail.feedbacks : [];
          this.feedbackList = list
            .filter((f) => f.student && f.student.name === this.user.name)
            .map((f) => ({
              id: f.id,
              type: f.difficultyType,
              time: '',
              content: f.description,
              reply: f.teacherResponse || null,
            }));
        }
      } catch (error) {
        console.error(error);
      }
    },
    backToDashboard() {
      this.currentCourse = null;
      this.currentCourseResources = [];
      this.feedbackList = [];
    },
    async fetchResources(courseId) {
      this.currentCourseResources = [];
      try {
        const res = await getResourceList(courseId);
        if (res.data.code === 200) {
          this.currentCourseResources = res.data.data;
        }
      } catch (error) {
        console.error('获取资源失败', error);
      }
    },
    async fetchFeedbackList(courseId) {
      try {
        const res = await getMyFeedbacks({
          courseId,
          studentName: this.user.name,
        });
        if (res.data.code === 200) {
          const list = Array.isArray(res.data.data) ? res.data.data : [];
          this.feedbackList = list.map((f) => ({
            id: f.id,
            type: f.difficultyType,
            time: '',
            content: f.description,
            reply: f.teacherResponse || null,
          }));
        }
      } catch (e) {
        console.error('获取我的反馈失败', e);
      }
    },
    async submitFeedback() {
      if (!this.feedbackForm.type || !this.feedbackForm.content) {
        alert('请填写完整的反馈信息');
        return;
      }
      try {
        const params = {
          courseId: this.currentCourse.id,
          studentName: this.user.name,
          difficultyType: this.feedbackForm.type,
          description: this.feedbackForm.content,
        };
        const res = await apiSubmitFeedback(params);
        if (res.data.code === 200) {
          await this.fetchFeedbackList(this.currentCourse.id);
          this.feedbackForm.type = '';
          this.feedbackForm.content = '';
          alert('反馈已提交');
        } else {
          alert(res.data.message || '提交失败');
        }
      } catch (error) {
        console.error(error);
        alert('系统错误或网络异常');
      }
    },
    viewReply(item) {
      if (item.reply) {
        this.currentReply = item.reply;
        this.showReplyModal = true;
      }
    },
    getTypeName(type) {
      const map = {
        CONCEPT: '概念模糊',
        CALCULATION: '计算困难',
        METHOD: '方法不当',
        concept: '概念理解',
        homework: '作业难题',
        lab: '实验操作',
        other: '其他',
      };
      return map[type] || '其他';
    },
    downloadResource(id) {
      window.open(getDownloadUrl(id));
    },
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
  padding-bottom: 40px;
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
  cursor: pointer;
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
  pointer-events: none; /* 让点击穿透到 card */
}

/* --- 详情页样式 --- */
.course-detail-view {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-panel {
  padding: 20px;
  position: relative;
}
.back-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background: #f4f4f5;
  border: 1px solid #e9e9eb;
  color: #909399;
  padding: 6px 15px;
  border-radius: 4px;
  cursor: pointer;
}
.back-btn:hover {
  background: #e9e9eb;
}
.panel-body {
  margin-top: 10px;
}
.flex-row {
  display: flex;
  gap: 20px;
}
.course-img-placeholder {
  width: 200px;
  height: 150px;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: #999;
}
.img-box {
  width: 100%;
  height: 100%;
  background-color: #dfe6ec;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 6px;
}
.course-svg {
  width: 100%;
  height: 100%;
}
.course-info-text {
  flex: 1;
}
.course-info-text h2 {
  margin: 0 0 15px 0;
  font-size: 24px;
  color: #333;
}
.info-row {
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}
.info-row .label {
  font-weight: bold;
  margin-right: 8px;
}
.info-row .score {
  color: #ff9900;
  font-weight: bold;
  font-size: 16px;
}
.desc-box {
  margin-top: 15px;
  background: #f9fafc;
  padding: 10px;
  border-radius: 4px;
}
.desc-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-top: 5px;
}

/* 反馈面板 */
.feedback-panel {
  padding: 20px;
  min-height: 300px;
}
.panel-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 15px;
}
.panel-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  border-left: 4px solid #409eff;
  padding-left: 10px;
}
.split-layout {
  display: flex;
  gap: 30px;
}
.feedback-form-section,
.feedback-list-section {
  flex: 1;
}
.feedback-form-section h4,
.feedback-list-section h4 {
  margin: 0 0 15px 0;
  color: #666;
  font-size: 15px;
}
.form-group {
  margin-bottom: 15px;
}
.form-group label {
  display: block;
  margin-bottom: 5px;
  font-size: 13px;
  color: #606266;
}
.custom-select,
.custom-textarea {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-family: inherit;
}
.custom-textarea {
  resize: vertical;
}

.feedback-list {
  max-height: 250px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
}
.feedback-item {
  padding: 10px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}
.feedback-item:hover {
  background: #f9f9f9;
}
.feedback-item.has-reply {
  background: #f0f9eb;
}
.feedback-item:last-child {
  border-bottom: none;
}
.fb-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}
.fb-type-tag {
  background: #e9e9eb;
  color: #909399;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
}
.fb-time {
  font-size: 12px;
  color: #c0c4cc;
}
.fb-content {
  font-size: 13px;
  color: #606266;
  margin: 5px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  /* 添加标准属性以提高兼容性 */
  display: -moz-box;
  display: box;
  line-clamp: 2;
  -moz-line-clamp: 2;
  box-orient: vertical;
  -moz-box-orient: vertical;
}
.fb-status {
  font-size: 12px;
  text-align: right;
}
.status-replied {
  color: #67c23a;
}
.status-pending {
  color: #e6a23c;
}
.empty-text {
  text-align: center;
  color: #999;
  padding: 20px;
}

/* 资源面板 */
.resource-panel {
  padding: 20px;
  min-height: 200px;
}
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
.empty-res-state {
  text-align: center;
  color: #999;
  padding: 30px;
  font-size: 14px;
}

/* 弹窗通用 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
  backdrop-filter: blur(2px);
}
.modal-content {
  background: white;
  padding: 25px;
  border-radius: 12px;
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
</style>
