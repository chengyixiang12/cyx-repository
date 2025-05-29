<template>
  <div class="page-container">
    <!-- 消息展示区 -->
    <div class="chat-box" ref="chatBox">
      <div v-for="(msg, index) in messages" :key="index" class="message-row" :class="msg.role">
        <el-tag :type="msg.role === 'user' ? 'success' : 'info'">
          {{ msg.role === 'user' ? '你' : 'AI' }}
        </el-tag>
        <div class="message-bubble markdown-body">
          <VueMarkdownIt :source="msg.content" />
        </div>
      </div>
    </div>

    <!-- 输入框和发送按钮 -->
    <div class="textarea-wrapper">
      <el-input
        v-model="input"
        type="textarea"
        placeholder="请输入消息"
        @keyup.enter="sendMessage"
        :autosize="{ minRows: 4, maxRows: 6 }"
        class="custom-textarea"
      />
      <el-button type="primary" class="send-btn" @click="sendMessage">发送</el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, nextTick } from 'vue'
import { getWebSocketInstance } from '@/utils/websocket'
import VueMarkdownIt from 'vue3-markdown-it'

interface Message {
  role: 'user' | 'assistant'
  content: string
}

const input = ref('')
const messages = ref<Message[]>([])
const chatBox = ref<HTMLElement>()
const wsInstance = getWebSocketInstance()

wsInstance.aiAnwser = (message) => {
  const last = messages.value[messages.value.length - 1]
  if (!last || last.role !== 'assistant') {
    messages.value.push({ role: 'assistant', content: '' })
  }
  messages.value[messages.value.length - 1].content += message.answer
  scrollToBottom()
}

const scrollToBottom = () => {
  nextTick(() => {
    chatBox.value?.scrollTo({ top: chatBox.value.scrollHeight, behavior: 'smooth' })
  })
}

const sendMessage = () => {
  const content = input.value.trim()
  if (!content) return
  messages.value.push({ role: 'user', content })
  input.value = ''
  scrollToBottom()
  wsInstance.send({ order: 'AI', question: content })
}
</script>

<style scoped>
@import 'github-markdown-css/github-markdown.css';

.page-container {
  display: flex;
  flex-direction: column;
  padding: 1rem;
  min-height: 83vh;
  box-sizing: border-box;
}

.chat-box {
  flex-grow: 1;
  height: 60vh; 
  overflow-y: auto;
  margin-bottom: 1rem;
}

.chat-box::-webkit-scrollbar {
  height: 6px;
  width: 5px;
}
.chat-box::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.message-row {
  margin-bottom: 0.5rem;
  position: relative;
  padding-top: 24px;
}

.message-row.user {
  text-align: right;
}

.message-row.assistant {
  text-align: left;
}

.message-row.user > .el-tag {
  position: absolute;
  top: 0;
  right: 0;
}

.message-row.assistant > .el-tag {
  position: absolute;
  top: 0;
  left: 0;
}

.message-bubble {
  display: inline-block;
  max-width: 80%;
  word-break: break-word;
  margin-top: 4px;
  margin-top: 4px;
}

.textarea-wrapper {
  position: relative;
}

.custom-textarea :deep(.el-textarea__inner) {
  padding-right: 5rem; /* 留出发送按钮位置 */
  min-height: 80px;
  resize: none;
}

.send-btn {
  position: absolute;
  bottom: 8px;
  right: 8px;
  z-index: 1;
}

/* markdown 样式定制 */
:deep(.markdown-body) {
  font-size: 14px;
  background-color: transparent;
  padding: 0;
  border-radius: 0;
}
</style>
