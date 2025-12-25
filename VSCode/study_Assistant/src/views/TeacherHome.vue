<template>
  <div class="teacher-home">
    <!-- 1. 顶部导航栏 -->
    <header class="navbar">
      <div class="nav-content">
        <div class="logo">AI 校园学习助手 - 教师端</div>
        <div class="nav-items">
          <span class="nav-link active">首页</span>
          <span class="nav-link" @click="$router.push('/ai')">AI 助手</span>

          <div class="user-profile">
            <span class="user-name">{{ user.name }}</span>
            <span class="role-badge">教师</span>
          </div>

          <button class="logout-btn" @click="handleLogout">退出</button>
        </div>
      </div>
    </header>

    <!-- 2. 下方主体布局 -->
    <div class="main-body">
      <!-- 左侧侧边栏 -->
      <aside class="sidebar">
        <div class="create-btn-wrapper">
          <button
            class="create-btn"
            :class="{ active: currentView === 'create' }"
            @click="switchToCreate"
          >
            <span class="icon">+</span> 新建课程
          </button>
        </div>

        <div class="sidebar-title">我的课程</div>

        <!-- 课程列表滚动区 -->
        <div class="course-list-scroll">
          <div v-if="myCourses.length === 0" class="empty-list-tip">
            暂无课程
          </div>
          <div
            v-for="course in myCourses"
            :key="course.id"
            class="course-item"
            :class="{
              active:
                currentView === 'dashboard' && currentCourseId === course.id,
            }"
            @click="selectCourse(course.id)"
          >
            <!-- 后端字段: courseCode, courseName -->
            <span class="course-code">{{ course.courseCode }}</span>
            <span class="course-name">{{ course.courseName }}</span>
          </div>
        </div>
      </aside>

      <!-- 右侧主要内容区 -->
      <main class="content-area">
        <!-- 视图 A: 新建课程表单 -->
        <div v-if="currentView === 'create'" class="create-panel card-shadow">
          <h2 class="panel-title">创建一个新的班级课程</h2>
          <div class="form-group">
            <label>课程名称</label>
            <input
              type="text"
              v-model="newCourseForm.courseName"
              class="custom-input"
              placeholder="例如：高级软件工程"
            />
          </div>
          <div class="form-group">
            <label>课程简介</label>
            <textarea
              rows="4"
              v-model="newCourseForm.description"
              class="custom-input"
              placeholder="请输入课程的主要内容描述..."
            ></textarea>
          </div>
          <div class="form-actions">
            <button
              class="primary-btn"
              @click="handleCreate"
              :disabled="loading"
            >
              {{ loading ? '创建中...' : '立即创建' }}
            </button>
          </div>
        </div>

        <!-- 视图 B: 课程详情仪表盘 -->
        <div
          v-if="currentView === 'dashboard' && courseDetailData"
          class="dashboard-container"
        >
          <!-- 顶部：课程信息头 -->
          <div class="course-header card-shadow">
            <div class="header-left">
              <h1>{{ courseDetailData.course.courseName }}</h1>
              <span class="code-tag"
                >课程码: {{ courseDetailData.course.courseCode }}</span
              >
            </div>
            <p class="course-desc">
              {{ courseDetailData.course.description || '暂无描述' }}
            </p>
          </div>

          <!-- 仪表盘网格布局 -->
          <div class="dashboard-grid">
            <!-- 左列：学情分析 -->
            <div class="left-column">
              <!-- 模块1: 难点分布柱状图 (前端计算) -->
              <div class="chart-card card-shadow">
                <div class="card-header">
                  <h3>难点类型分布</h3>
                  <span class="sub-text">基于学生反馈自动生成</span>
                </div>
                <div class="bar-chart-area">
                  <div v-if="computedStats.length === 0" class="no-data-text">
                    暂无反馈数据
                  </div>
                  <div
                    v-else
                    v-for="(item, index) in computedStats"
                    :key="index"
                    class="bar-row"
                  >
                    <span class="bar-label">{{ item.label }}</span>
                    <div class="progress-track">
                      <div
                        class="progress-fill"
                        :style="{
                          width: item.percent + '%',
                          backgroundColor: item.color,
                        }"
                      ></div>
                    </div>
                    <span class="bar-value"
                      >{{ item.count }}人 ({{ item.percent }}%)</span
                    >
                  </div>
                </div>
              </div>

              <!-- 模块2: 难点反馈列表 -->
              <div class="feedback-card card-shadow">
                <div class="card-header">
                  <h3>学生难点反馈</h3>
                </div>
                <div class="feedback-list-scroll">
                  <div
                    v-if="
                      !courseDetailData.feedbacks ||
                      courseDetailData.feedbacks.length === 0
                    "
                    class="no-data-text"
                  >
                    暂无反馈
                  </div>
                  <div
                    v-for="fb in courseDetailData.feedbacks"
                    :key="fb.id"
                    class="feedback-item"
                  >
                    <div class="fb-top">
                      <!-- difficultyType 是后端的枚举字符串，需要映射 -->
                      <span
                        class="tag"
                        :style="{ background: getTypeColor(fb.difficultyType) }"
                      >
                        {{ getTypeLabel(fb.difficultyType) }}
                      </span>
                      <span class="student-name">{{
                        fb.studentName || '匿名学生'
                      }}</span>
                    </div>
                    <p class="fb-content">{{ fb.description }}</p>

                    <!-- 回复区域 -->
                    <div class="reply-wrapper">
                      <!-- 已回复 -->
                      <div v-if="fb.teacherResponse" class="replied-box">
                        <strong>教师回复：</strong> {{ fb.teacherResponse }}
                      </div>
                      <!-- 未回复 -->
                      <div v-else class="reply-box">
                        <input
                          type="text"
                          v-model="replyInputs[fb.id]"
                          placeholder="输入解答回复..."
                          class="mini-input"
                        />
                        <button class="mini-btn" @click="handleReply(fb.id)">
                          回复
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 右列：资源与成绩 -->
            <div class="right-column">
              <!-- 模块3: 成绩表 (带打分功能) -->
              <div class="score-card card-shadow">
                <div class="card-header">
                  <h3>班级成员 & 成绩</h3>
                </div>
                <div class="table-wrapper">
                  <table class="simple-table">
                    <thead>
                      <tr>
                        <th>姓名</th>
                        <th style="width: 140px">成绩</th>
                        <th style="width: 80px">操作</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="(stu, index) in courseDetailData.students"
                        :key="index"
                      >
                        <td>{{ stu.name }}</td>
                        <td>
                          <!-- 编辑模式 -->
                          <div
                            v-if="editingStudentName === stu.name"
                            class="score-edit-box"
                          >
                            <input
                              type="number"
                              v-model="tempScore"
                              class="mini-input score-input"
                              min="0"
                              max="100"
                            />
                          </div>
                          <!-- 展示模式 -->
                          <div v-else>
                            <span
                              :class="
                                stu.score >= 60
                                  ? 'score-pass'
                                  : stu.score !== null
                                    ? 'score-fail'
                                    : 'score-null'
                              "
                            >
                              {{ stu.score !== null ? stu.score : '未评分' }}
                            </span>
                          </div>
                        </td>
                        <td>
                          <!-- 编辑模式下的按钮 -->
                          <div
                            v-if="editingStudentName === stu.name"
                            class="action-btns"
                          >
                            <button
                              class="icon-btn save"
                              @click="submitScore(stu.name)"
                              title="保存"
                            >
                              ✓
                            </button>
                            <button
                              class="icon-btn cancel"
                              @click="cancelEdit"
                              title="取消"
                            >
                              ✕
                            </button>
                          </div>
                          <!-- 展示模式下的按钮 -->
                          <div v-else>
                            <button class="text-btn" @click="startEdit(stu)">
                              {{ stu.score !== null ? '修改' : '打分' }}
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr
                        v-if="
                          !courseDetailData.students ||
                          courseDetailData.students.length === 0
                        "
                      >
                        <td colspan="3" style="text-align: center; color: #999">
                          暂无学生加入
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- 模块4: 教学资源 -->
              <div class="resource-card card-shadow">
                <div class="card-header">
                  <h3>教学资源</h3>
                </div>
                <div class="upload-area">
                  <!-- 隐藏的文件输入框 -->
                  <input
                    type="file"
                    ref="fileInput"
                    @change="handleFileChange"
                    style="display: none"
                  />

                  <div class="upload-row mb-10">
                    <button class="text-btn" @click="triggerFileSelect">
                      {{
                        selectedFile ? selectedFile.name : '📄 点击选择文件...'
                      }}
                    </button>
                  </div>

                  <div class="upload-row">
                    <select v-model="uploadForm.type" class="mini-select">
                      <option value="PPT">PPT课件</option>
                      <option value="VIDEO">视频资源</option>
                      <option value="DOCUMENT">文档资料</option>
                      <option value="OTHER">其他资料</option>
                    </select>
                    <button class="primary-btn small" @click="handleUpload">
                      上传
                    </button>
                  </div>
                </div>

                <div class="resource-list-scroll">
                  <div v-if="resourceList.length === 0" class="no-data-text">
                    暂无资源
                  </div>
                  <div
                    v-for="res in resourceList"
                    :key="res.id"
                    class="resource-item"
                  >
                    <span class="res-icon">{{
                      res.resourceType === 'VIDEO'
                        ? '🎥'
                        : res.resourceType === 'DOCUMENT'
                          ? '📚'
                          : '📄'
                    }}</span>
                    <div class="res-info">
                      <div class="res-name">{{ res.resourceName }}</div>
                      <div class="res-type">{{ res.resourceType }}</div>
                    </div>
                    <button class="text-btn" @click="downloadResource(res.id)">
                      下载
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import config from '@/api/config';
import { logout } from '@/api/sys/auth';
// 引入你提供的 API
import {
  getTeacherCourses,
  createCourse,
  getCourseDetail,
  replyFeedback,
  gradeStudent,
} from '@/api/sys/course';

import {
  uploadResource,
  getResourceList,
  getDownloadUrl,
} from '@/api/sys/resource';
export default {
  name: 'TeacherHome',
  data() {
    return {
      user: config.user.get() || { name: '教师' },
      currentView: 'create', // 'create' | 'dashboard'
      loading: false,

      // 创建课程表单
      newCourseForm: {
        courseName: '',
        description: '',
      },

      // 课程列表 (从后端获取)
      myCourses: [],
      currentCourseId: null,

      // 课程详情数据 (包含 course, feedbacks, students)
      courseDetailData: null,

      // 存储每个反馈的输入框内容 { feedbackId: "回复内容" }
      replyInputs: {},

      // 难点类型字典 (后端可能传回 Enum 字符串)
      difficultyMap: {
        CONCEPT: { label: '概念模糊', color: '#409EFF' },
        CALCULATION: { label: '计算错误', color: '#F56C6C' },
        METHOD: { label: '方法不当', color: '#E6A23C' },
      },

      resourceList: [], // 资源列表
      uploadForm: {
        title: '', // 暂时没用，直接用文件名
        type: 'PPT',
      },
      selectedFile: null, // 选中的文件
      editingStudentName: null, // 当前正在编辑的学生名字
      tempScore: '', // 临时存放输入的成绩
    };
  },
  computed: {
    // 根据 feedbacks 动态计算柱状图数据
    computedStats() {
      if (!this.courseDetailData || !this.courseDetailData.feedbacks) return [];

      const feedbacks = this.courseDetailData.feedbacks;
      const total = feedbacks.length;
      if (total === 0) return [];

      // 统计各类型数量
      const counts = {};
      feedbacks.forEach((fb) => {
        // 如果后端传回的类型不在字典里，归为 OTHER
        const type = fb.difficultyType;
        if (this.difficultyMap[type]) {
          counts[type] = (counts[type] || 0) + 1;
        }
      });

      // 转换为数组用于渲染
      return Object.keys(counts).map((type) => {
        const count = counts[type];
        const conf = this.difficultyMap[type];
        return {
          label: conf.label,
          color: conf.color,
          count: count,
          percent: Math.round((count / total) * 100),
        };
      });
    },
  },
  created() {
    console.log('🎯 TeacherHome组件被创建');
    const localUser = config.user.get();
    console.log('🔍 从本地存储获取的用户信息:', localUser);
    
    if (!localUser) {
      console.warn('⚠️ 用户信息不存在，重定向到登录页面');
      this.$router.push('/login');
      return;
    }
    
    this.user = localUser;
    console.log('✅ 用户信息已设置:', this.user);
    console.log('📋 开始获取教师课程列表...');
    
    // 初始化加载教师课程列表
    this.fetchMyCourses();
  },
  methods: {
    // 1. 获取教师创建的课程列表
    async fetchMyCourses() {
      try {
        // 对应 Controller: getTeacherCourses (@RequestBody Map params)
        const res = await getTeacherCourses({ teacherName: this.user.name });
        if (res.data.code === 200) {
          this.myCourses = res.data.data || [];
          // 如果有课程，默认选中第一个
          if (this.myCourses.length > 0 && this.currentView === 'dashboard') {
            this.selectCourse(this.myCourses[0].id);
          }
        }
      } catch (e) {
        console.error('获取课程列表失败', e);
      }
    },

    // 2. 切换到新建视图
    switchToCreate() {
      this.currentView = 'create';
      this.currentCourseId = null;
      this.courseDetailData = null;
    },

    // 3. 选中课程 -> 获取详情
    async selectCourse(courseId) {
      this.currentView = 'dashboard';
      this.currentCourseId = courseId;
      this.courseDetailData = null; // 清空旧数据防止闪烁

      this.cancelEdit(); // 切换课程时，重置打分状态

      try {
        // 对应 Controller: getCourseDetail (@PathVariable courseId)
        const res = await getCourseDetail(courseId);
        if (res.data.code === 200) {
          // res.data.data 结构: { course: {}, feedbacks: [], students: [] }
          this.courseDetailData = res.data.data;
          this.fetchResources(courseId);
        }
      } catch (e) {
        console.error('获取课程详情失败', e);
      }
    },

    // 4. 创建课程
    async handleCreate() {
      if (!this.newCourseForm.courseName) return alert('请输入课程名称');

      this.loading = true;
      try {
        // 对应 Controller: createCourse
        const params = {
          courseName: this.newCourseForm.courseName,
          description: this.newCourseForm.description,
          teacherName: this.user.name,
          // courseCode 由后端生成，不需要传
        };
        const res = await createCourse(params);

        if (res.data.code === 200) {
          alert('课程创建成功！');
          this.newCourseForm = { courseName: '', description: '' }; // 重置表单
          await this.fetchMyCourses(); // 刷新侧边栏
          // 自动跳转到新创建的课程（假设后端返回了新ID最好，没有的话就切回列表第一个）
          this.currentView = 'create'; // 或者逻辑可以优化为跳转到最新课程
        } else {
          alert(res.data.message || '创建失败');
        }
      } catch (e) {
        console.error(e);
        alert('系统错误');
      } finally {
        this.loading = false;
      }
    },

    // 5. 回复反馈
    async handleReply(feedbackId) {
      const responseText = this.replyInputs[feedbackId];
      if (!responseText) return alert('请输入回复内容');

      try {
        // 对应 Controller: replyFeedback
        const params = {
          feedbackId: feedbackId,
          response: responseText,
        };
        const res = await replyFeedback(params);
        if (res.data.code === 200) {
          alert('回复成功');
          // 刷新当前详情，以显示回复结果
          this.selectCourse(this.currentCourseId);
          this.replyInputs[feedbackId] = ''; // 清空输入框
        } else {
          alert(res.data.message);
        }
      } catch (e) {
        alert('回复失败');
      }
    },
    //6.选择文件触发
    triggerFileSelect() {
      this.$refs.fileInput.click();
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.selectedFile = file;
        // 自动填充标题
        this.uploadForm.title = file.name;
      }
    },
    // 7. 执行上传
    async handleUpload() {
      if (!this.selectedFile) return alert('请先选择文件');
      if (!this.currentCourseId) return alert('未选中课程');

      const formData = new FormData();
      formData.append('file', this.selectedFile);
      formData.append('courseId', this.currentCourseId);
      formData.append('resourceType', this.uploadForm.type);

      try {
        const res = await uploadResource(formData);
        if (res.data.code === 200) {
          alert('上传成功');
          this.selectedFile = null;
          this.$refs.fileInput.value = ''; // 清空 input
          this.fetchResources(this.currentCourseId); // 刷新列表
        } else {
          alert('上传失败: ' + res.data.message);
        }
      } catch (e) {
        console.error(e);
        alert('上传出错');
      }
    },

    // 8. 获取资源列表
    async fetchResources(courseId) {
      const res = await getResourceList(courseId);
      if (res.data.code === 200) {
        this.resourceList = res.data.data;
      }
    },

    // 9. 下载
    downloadResource(id) {
      window.open(getDownloadUrl(id));
    },

    startEdit(student) {
      this.editingStudentName = student.name;
      this.tempScore = student.score !== null ? student.score : '';
    },

    // 2. 取消编辑
    cancelEdit() {
      this.editingStudentName = null;
      this.tempScore = '';
    },

    // 3. 提交成绩
    async submitScore(studentName) {
      // 简单校验
      if (this.tempScore === '' || this.tempScore < 0 || this.tempScore > 100) {
        return alert('请输入 0-100 之间的有效分数');
      }

      try {
        const params = {
          courseId: this.currentCourseId,
          studentName: studentName,
          score: this.tempScore,
        };

        const res = await gradeStudent(params);

        if (res.data.code === 200) {
          // alert("打分成功"); // 体验优化：成功后不弹窗，直接刷新
          this.cancelEdit();
          // 刷新当前课程详情，以更新列表显示
          this.selectCourse(this.currentCourseId);
        } else {
          alert(res.data.message);
        }
      } catch (e) {
        console.error(e);
        alert('打分失败');
      }
    },

    // 工具：获取难点类型颜色
    getTypeColor(type) {
      return this.difficultyMap[type]
        ? this.difficultyMap[type].color
        : '#909399';
    },
    // 工具：获取难点类型中文
    getTypeLabel(type) {
      return this.difficultyMap[type]
        ? this.difficultyMap[type].label
        : '未知类型';
    },

    handleLogout() {
      if (confirm('确定退出登录吗？')) {
        logout();
        this.$router.push('/login');
      }
    },
  },
};
</script>

<style scoped>
/* 此处样式与之前保持一致，只做微调适配真实数据 */
.teacher-home {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue',
    Arial, sans-serif;
}

/* Navbar */
.navbar {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  height: 64px;
  flex-shrink: 0;
  z-index: 100;
}
.nav-content {
  max-width: 100%;
  padding: 0 30px;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
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
}
.nav-link.active {
  color: #409eff;
  font-weight: 500;
}
.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}
.role-badge {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  border: 1px solid #d9ecff;
  font-size: 12px;
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

/* Layout */
.main-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}
.sidebar {
  width: 240px;
  background: white;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
  padding: 20px 0;
}
.create-btn-wrapper {
  padding: 0 20px 20px 20px;
  border-bottom: 1px solid #f0f0f0;
}
.create-btn {
  width: 100%;
  background-color: #409eff;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.create-btn:hover {
  opacity: 0.9;
}
.create-btn.active {
  box-shadow: inset 0 2px 5px rgba(0, 0, 0, 0.2);
}
.sidebar-title {
  padding: 20px 20px 10px;
  font-size: 12px;
  color: #909399;
  font-weight: bold;
  text-transform: uppercase;
}
.course-list-scroll {
  flex: 1;
  overflow-y: auto;
}
.empty-list-tip {
  text-align: center;
  color: #999;
  font-size: 13px;
  margin-top: 20px;
}

.course-item {
  padding: 12px 20px;
  cursor: pointer;
  border-left: 3px solid transparent;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 10px;
}
.course-item:hover {
  background-color: #f5f7fa;
}
.course-item.active {
  background-color: #ecf5ff;
  border-left-color: #409eff;
}
.course-item.active .course-name {
  color: #409eff;
  font-weight: 500;
}
.course-code {
  background: #f0f2f5;
  color: #606266;
  font-size: 12px;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: monospace;
}
.course-name {
  font-size: 14px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Content */
.content-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
.card-shadow {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

/* Create View */
.create-panel {
  max-width: 600px;
  margin: 40px auto;
}
.panel-title {
  margin-top: 0;
  color: #303133;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 20px;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #303133;
}
.custom-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-sizing: border-box;
  transition: border 0.3s;
}
.custom-input:focus {
  border-color: #409eff;
  outline: none;
}
.primary-btn {
  background: #409eff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}
.primary-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

/* Dashboard View */
.dashboard-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.course-header h1 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}
.code-tag {
  background: #e6f7ff;
  color: #1890ff;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 13px;
  border: 1px solid #91d5ff;
}
.course-desc {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.dashboard-grid {
  display: flex;
  gap: 20px;
}
.left-column {
  flex: 7;
  display: flex;
  flex-direction: column;
}
.right-column {
  flex: 3;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 10px;
}
.card-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}
.sub-text {
  font-size: 12px;
  color: #909399;
}

/* Chart */
.bar-chart-area {
  min-height: 100px;
}
.bar-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.bar-label {
  width: 70px;
  font-size: 13px;
  color: #606266;
}
.progress-track {
  flex: 1;
  height: 12px;
  background: #ebeef5;
  border-radius: 6px;
  margin: 0 15px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  border-radius: 6px;
  transition: width 0.6s ease;
}
.bar-value {
  width: 80px;
  font-size: 12px;
  color: #909399;
  text-align: right;
}
.no-data-text {
  text-align: center;
  color: #999;
  padding: 20px;
  font-size: 14px;
}

/* Feedback */
.feedback-list-scroll {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 5px;
}
.feedback-item {
  background: #f9fafc;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 10px;
  border: 1px solid #ebeef5;
}
.fb-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.tag {
  font-size: 12px;
  color: white;
  padding: 2px 6px;
  border-radius: 3px;
}
.student-name {
  font-size: 12px;
  color: #909399;
}
.fb-content {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #303133;
}

.reply-wrapper {
  margin-top: 10px;
}
.replied-box {
  background: #e1f3d8;
  color: #67c23a;
  padding: 8px;
  border-radius: 4px;
  font-size: 13px;
}
.reply-box {
  display: flex;
  gap: 10px;
}
.mini-input {
  flex: 1;
  padding: 5px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
.mini-btn {
  padding: 5px 10px;
  border: 1px solid #dcdfe6;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  color: #606266;
}
.mini-btn:hover {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

/* Score Table */
.table-wrapper {
  max-height: 300px;
  overflow-y: auto;
}
.simple-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.simple-table th {
  text-align: left;
  padding: 8px;
  color: #909399;
  border-bottom: 1px solid #ebeef5;
  font-weight: normal;
}
.simple-table td {
  padding: 10px 8px;
  border-bottom: 1px solid #f5f7fa;
  color: #606266;
}
.score-pass {
  color: #67c23a;
  font-weight: bold;
}
.score-fail {
  color: #f56c6c;
  font-weight: bold;
}

/* Resources */
.upload-area {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #eee;
}
.mb-10 {
  margin-bottom: 10px;
}
.upload-row {
  display: flex;
  gap: 5px;
}
.mini-select {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 0 5px;
}
.primary-btn.small {
  padding: 5px 10px;
  font-size: 12px;
}
.resource-list-scroll {
  max-height: 250px;
  overflow-y: auto;
}
.resource-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f7fa;
}
.res-icon {
  font-size: 20px;
  margin-right: 10px;
}
.res-info {
  flex: 1;
  overflow: hidden;
}
.res-name {
  font-size: 13px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.res-type {
  font-size: 12px;
  color: #909399;
}
.text-btn {
  border: none;
  background: none;
  color: #409eff;
  cursor: pointer;
  font-size: 13px;
}
.text-btn:hover {
  text-decoration: underline;
}

/* Scrollbar */
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-thumb {
  background: #e0e3e9;
  border-radius: 3px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
.score-null {
  color: #909399;
  font-style: italic;
  font-size: 12px;
}

.score-edit-box {
  display: flex;
  align-items: center;
}
.score-input {
  width: 60px;
  padding: 4px;
  text-align: center;
}

.action-btns {
  display: flex;
  gap: 5px;
}
.icon-btn {
  border: none;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}
.icon-btn.save {
  background: #67c23a;
  color: white;
}
.icon-btn.save:hover {
  background: #85ce61;
}
.icon-btn.cancel {
  background: #f56c6c;
  color: white;
}
.icon-btn.cancel:hover {
  background: #f78989;
}
</style>
