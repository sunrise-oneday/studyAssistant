<template>
  <div class="ai-assistant">
    <header class="navbar">
      <div class="nav-content">
        <div class="logo">AI 助手</div>
        <button class="back-btn" @click="$router.back()">返回</button>
      </div>
    </header>
    <div class="chat-container">
      <div class="chat-window card-shadow">
        <div class="messages">
          <div v-for="(msg, idx) in messages" :key="idx" class="msg-row" :class="msg.role">
            <div class="bubble">
              {{ msg.content }}
            </div>
          </div>
          <div v-if="loading" class="msg-row assistant">
            <div class="bubble">思考中...</div>
          </div>
        </div>
        <div class="input-area">
          <input
            v-model="input"
            class="chat-input"
            placeholder="请输入你的问题，例如：帮我解释递归算法"
            @keyup.enter="send"
          />
          <button class="primary-btn" :disabled="loading || !input.trim()" @click="send">
            发送
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import config from '@/api/config';

export default {
  name: 'AI_Assistant',
  data() {
    return {
      messages: [],
      input: '',
      loading: false,
      systemPrompt: 'You are a helpful assistant.',
    };
  },
  methods: {
    async send() {
      if (!this.input.trim()) return;
      const userText = this.input.trim();
      this.messages.push({ role: 'user', content: userText });
      this.input = '';
      this.loading = true;
      try {
        const res = await axios.post(config.host + 'ai/chat', {
          prompt: userText,
          system: this.systemPrompt,
        });
        if (res.data && res.data.code === 200) {
          const data = res.data.data || {};
          // 优先显示解析出的 text，否则显示原始 JSON
          const reply = data.text || data.raw || '暂无回复';
          this.messages.push({ role: 'assistant', content: reply });
        } else {
          const msg = (res.data && res.data.message) || '调用失败';
          this.messages.push({ role: 'assistant', content: '错误：' + msg });
        }
      } catch (e) {
        this.messages.push({ role: 'assistant', content: '网络错误或服务异常' });
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.ai-assistant {
  min-height: 100vh;
  background: #f0f2f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
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
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
}
.back-btn {
  background: #f4f4f5;
  border: 1px solid #e9e9eb;
  color: #909399;
  padding: 6px 15px;
  border-radius: 4px;
  cursor: pointer;
}
.chat-container {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;
}
.card-shadow {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}
.chat-window {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 140px);
  padding: 16px;
}
.messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}
.msg-row {
  display: flex;
  margin-bottom: 10px;
}
.msg-row.user {
  justify-content: flex-end;
}
.msg-row.assistant {
  justify-content: flex-start;
}
.bubble {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 14px;
  line-height: 1.5;
  font-size: 14px;
  color: #333;
  white-space: pre-wrap;
}
.msg-row.user .bubble {
  background: #ecf5ff;
  color: #409eff;
}
.msg-row.assistant .bubble {
  background: #f9fafc;
  color: #606266;
}
.input-area {
  display: flex;
  gap: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}
.chat-input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  outline: none;
}
.primary-btn {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
}
.primary-btn:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}
</style>
