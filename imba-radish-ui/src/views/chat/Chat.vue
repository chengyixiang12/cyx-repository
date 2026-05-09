<template>
  <div class="page-container">
    <!-- 消息展示区 -->
    <div class="chat-box" ref="chatBox">
      <div v-for="msg in messages" :key="msg.id" class="message-row" :class="msg.role">
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
      <el-input v-model="input" type="textarea" placeholder="请输入消息" @keyup.enter="sendMessage"
        :autosize="{ minRows: 4, maxRows: 6 }" class="custom-textarea" />
      <el-button type="primary" class="send-btn" @click="sendMessage">发送</el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, nextTick, onMounted } from 'vue'
import { getWebSocketInstance } from '@/utils/websocket'
import VueMarkdownIt from 'vue3-markdown-it'
import { saveDialogueApi } from '@/api/dialogueHistory'
import { SaveDialogueRequest } from '@/types/dialogueHistory'
import { WebsocketMessage } from '@/utils/websocketManager'

interface Message {
  id: string
  role: 'user' | 'assistant'
  content: string
}

const input = ref('')
const messages = ref<Message[]>([])
const chatBox = ref<HTMLElement>()
const wsInstance = getWebSocketInstance()
const dialogue = ref<SaveDialogueRequest>({
  title: null
})
const dialogueId = ref<number | null>(null)
const isLoading = ref(false)

wsInstance.aiAnwser = (message: WebsocketMessage) => {
  const last = messages.value[messages.value.length - 1]

  if (isLoading.value && last && last.role === 'assistant' && last.content === 'AI 正在输入中...') {
    last.content = ''
  }

  if (!last || last.role !== 'assistant') {
    messages.value.push({ id: generateMessageId(), role: 'assistant', content: '' })
  }
  messages.value[messages.value.length - 1].content += message.answer
  scrollToBottom()
}

// 生成唯一消息ID
const generateMessageId = () => {
  return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}

const scrollToBottom = async () => {
  nextTick(() => {
    chatBox.value?.scrollTo({ top: chatBox.value.scrollHeight, behavior: 'smooth' })
  })
}

const sendMessage = async () => {
  if (!dialogueId.value || dialogueId.value == null) {
    await saveDialogue();
  }
  const content = input.value.trim()
  if (!content) return
  messages.value.push({ id: generateMessageId(), role: 'user', content })
  input.value = ''

  // 添加"AI 正在输入..."提示
  messages.value.push({ id: generateMessageId(), role: 'assistant', content: 'AI 正在输入中...' })
  isLoading.value = true

  await scrollToBottom()
  wsInstance.send({ order: 'AI', question: content, dialogueId: dialogueId.value })
}

// 新增对话
const saveDialogue = async () => {
  dialogue.value.title = '新对话'
  dialogueId.value = await saveDialogueApi(dialogue.value)
}

onMounted(() => {
})
</script>

<style scoped>
@import 'github-markdown-css/github-markdown.css';

.page-container {
  display: flex;
  flex-direction: column;
  padding: 1rem;
  min-height: 83vh;
  box-sizing: border-box;
  background-color: #f5f7fa;
}

.chat-box {
  flex-grow: 1;
  height: 60vh;
  overflow-y: auto;
  margin-bottom: 1rem;
  padding: 1rem;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.chat-box::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}

.chat-box::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.chat-box::-webkit-scrollbar-track {
  background-color: #f1f1f1;
  border-radius: 3px;
}

.message-row {
  margin-bottom: 1.5rem;
  position: relative;
  padding-top: 24px;
}

.message-row.user {
  text-align: right;
}

.message-row.assistant {
  text-align: left;
}

.message-row.user>.el-tag {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #67c23a;
  border-color: #67c23a;
}

.message-row.assistant>.el-tag {
  position: absolute;
  top: 0;
  left: 0;
  background-color: #409eff;
  border-color: #409eff;
}

.message-bubble {
  display: inline-block;
  max-width: 80%;
  word-break: break-word;
  margin-top: 8px;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message-row.user .message-bubble {
  background-color: #f0f9eb;
  border-bottom-right-radius: 4px;
}

.message-row.assistant .message-bubble {
  background-color: #ecf5ff;
  border-bottom-left-radius: 4px;
}

.textarea-wrapper {
  position: relative;
  background-color: #ffffff;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.custom-textarea :deep(.el-textarea__inner) {
  padding-right: 5rem;
  min-height: 80px;
  resize: none;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  transition: border-color 0.3s;
}

.custom-textarea :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.send-btn {
  position: absolute;
  bottom: 20px;
  right: 20px;
  z-index: 1;
  border-radius: 6px;
}

/* markdown 样式定制 */
:deep(.markdown-body) {
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
  background-color: transparent;
  padding: 0;
  border-radius: 0;
}

:deep(.markdown-body h1),
:deep(.markdown-body h2),
:deep(.markdown-body h3),
:deep(.markdown-body h4),
:deep(.markdown-body h5),
:deep(.markdown-body h6) {
  margin-top: 16px;
  margin-bottom: 8px;
  font-weight: 600;
  line-height: 1.4;
  color: #303133;
}

:deep(.markdown-body h1) {
  font-size: 20px;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 8px;
}

:deep(.markdown-body h2) {
  font-size: 18px;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 6px;
}

:deep(.markdown-body h3) {
  font-size: 16px;
}

:deep(.markdown-body p) {
  margin-top: 8px;
  margin-bottom: 8px;
}

:deep(.markdown-body ul),
:deep(.markdown-body ol) {
  margin-top: 8px;
  margin-bottom: 8px;
  padding-left: 24px;
}

:deep(.markdown-body li) {
  margin-top: 4px;
  margin-bottom: 4px;
}

:deep(.markdown-body code) {
  padding: 2px 4px;
  font-size: 0.9em;
  background-color: #f6f8fa;
  border-radius: 3px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

:deep(.markdown-body pre) {
  padding: 12px;
  margin-top: 8px;
  margin-bottom: 8px;
  background-color: #f6f8fa;
  border-radius: 6px;
  overflow-x: auto;
  font-size: 0.9em;
}

:deep(.markdown-body pre code) {
  padding: 0;
  background-color: transparent;
  border-radius: 0;
}

:deep(.markdown-body blockquote) {
  padding: 10px 16px;
  margin-top: 8px;
  margin-bottom: 8px;
  border-left: 4px solid #dfe2e5;
  background-color: #f6f8fa;
  color: #6a737d;
}

:deep(.markdown-body table) {
  border-collapse: collapse;
  margin-top: 8px;
  margin-bottom: 8px;
  width: 100%;
  font-size: 0.9em;
}

:deep(.markdown-body th) {
  background-color: #f6f8fa;
  border: 1px solid #dfe2e5;
  padding: 6px 13px;
  text-align: left;
  font-weight: 600;
}

:deep(.markdown-body td) {
  border: 1px solid #dfe2e5;
  padding: 6px 13px;
}

:deep(.markdown-body tr:nth-child(even)) {
  background-color: #f6f8fa;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .page-container {
    padding: 0.5rem;
  }
  
  .chat-box {
    padding: 0.5rem;
    height: 70vh;
  }
  
  .message-bubble {
    max-width: 90%;
    padding: 10px 14px;
  }
  
  .textarea-wrapper {
    padding: 0.5rem;
  }
  
  .send-btn {
    bottom: 12px;
    right: 12px;
  }
}
</style>
