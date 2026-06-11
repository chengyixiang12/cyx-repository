<template>
  <div class="page-container">
    <!-- 工具栏 -->
    <!-- <div class="chat-toolbar">
      <el-button size="small" @click="clearChat" :disabled="messages.length === 0">
        <el-icon><Delete /></el-icon> 清空聊天
      </el-button>
    </div> -->

    <!-- 消息展示区 -->
    <div class="chat-box" ref="chatBox">
      <!-- 空状态 -->
      <div v-if="messages.length === 0" class="empty-state">
        <el-empty description="暂无消息，开始对话吧" />
      </div>

      <div v-for="msg in messages" :key="msg.id" class="message-row" :class="msg.role">
        <el-tag :type="msg.role === 'user' ? 'success' : 'info'">
          {{ msg.role === 'user' ? '你' : 'AI' }}
        </el-tag>
        <div v-if="msg.status === 'error'" class="error-tip">
          <el-icon><WarningFilled /></el-icon>
          <span @click="retryMessage(msg)">重发</span>
        </div>
        <div class="message-bubble markdown-body">
          <VueMarkdownIt :source="msg.content" />
        </div>
      </div>
    </div>

    <!-- 输入框和发送按钮 -->
    <div class="textarea-wrapper">
      <el-input v-model="input" type="textarea" placeholder="请输入消息，按 Enter 发送" @keyup.enter="sendMessage"
        :autosize="{ minRows: 4, maxRows: 6 }" class="custom-textarea" :disabled="isLoading" />
      <el-button type="primary" class="send-btn" @click="sendMessage" :disabled="isLoading || !input.trim()">
        <span v-if="isLoading">发送中...</span>
        <span v-else>发送</span>
      </el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, WarningFilled } from '@element-plus/icons-vue'
import { getWebSocketInstance } from '@/utils/websocket'
import VueMarkdownIt from 'vue3-markdown-it'
import { saveDialogueApi } from '@/api/dialogueHistory'
import { SaveDialogueRequest } from '@/types/dialogueHistory'
import { WebsocketMessage } from '@/utils/websocketManager'

interface Message {
  id: string
  role: 'user' | 'assistant'
  content: string
  status?: 'sending' | 'error' | 'done'
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

// 生成唯一消息ID
const generateMessageId = () => {
  return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}

const scrollToBottom = () => {
  nextTick(() => {
    chatBox.value?.scrollTo({ top: chatBox.value.scrollHeight, behavior: 'smooth' })
  })
}

// 清空聊天
const clearChat = () => {
  messages.value = []
  dialogueId.value = null
  dialogue.value.title = null
  ElMessage.success('已清空聊天记录')
}

// 重发消息
const retryMessage = async (msg: Message) => {
  if (msg.role !== 'user' || msg.status !== 'error') return
  
  msg.status = 'sending'
  messages.value.push({ id: generateMessageId(), role: 'assistant', content: 'AI 正在输入中...', status: 'sending' })
  isLoading.value = true
  scrollToBottom()

  try {
    wsInstance.send({ order: 'AI', question: msg.content, dialogueId: dialogueId.value })
  } catch {
    isLoading.value = false
  }
}

const sendMessage = async () => {
  const content = input.value.trim()
  if (!content || isLoading.value) return

  if (!dialogueId.value) {
    try {
      await saveDialogue();
    } catch (error) {
      ElMessage.error('创建对话失败，请重试')
      return
    }
  }

  const userMsg: Message = { id: generateMessageId(), role: 'user', content, status: 'done' }
  messages.value.push(userMsg)
  input.value = ''

  // 添加"AI 正在输入..."提示
  const aiMsg: Message = { id: generateMessageId(), role: 'assistant', content: 'AI 正在输入中...', status: 'sending' }
  messages.value.push(aiMsg)
  isLoading.value = true

  scrollToBottom()

  try {
    wsInstance.send({ order: 'AI', question: content, dialogueId: dialogueId.value })
  } catch (error) {
    isLoading.value = false
    userMsg.status = 'error'
    aiMsg.content = '发送失败，请检查网络后重试'
    aiMsg.status = 'error'
    ElMessage.error('发送消息失败')
  }
}

// 新增对话
const saveDialogue = async () => {
  dialogue.value.title = '新对话'
  dialogueId.value = await saveDialogueApi(dialogue.value)
}

// 监听 WebSocket 错误
wsInstance.onError = () => {
  isLoading.value = false
  const last = messages.value[messages.value.length - 1]
  if (last && last.role === 'assistant' && last.status === 'sending') {
    last.content = '连接失败，请检查网络'
    last.status = 'error'
  }
  ElMessage.error('WebSocket 连接错误')
}

wsInstance.aiAnwser = (message: WebsocketMessage) => {
  const last = messages.value[messages.value.length - 1]

  if (isLoading.value && last && last.role === 'assistant' && last.content === 'AI 正在输入中...') {
    last.content = ''
    last.status = 'done'
  }

  if (!last || last.role !== 'assistant') {
    messages.value.push({ id: generateMessageId(), role: 'assistant', content: '', status: 'done' })
  }
  messages.value[messages.value.length - 1].content += message.answer
  isLoading.value = false
  scrollToBottom()
}
</script>

<style scoped>
@import 'github-markdown-css/github-markdown.css';

.page-container {
  display: flex;
  flex-direction: column;
  padding: 1rem;
  min-height: calc(100vh - 160px);
  max-height: calc(100vh - 160px);
  box-sizing: border-box;
  background-color: #f5f7fa;
  overflow: hidden;
}

.chat-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 300px;
}

.chat-box {
  flex-grow: 1;
  overflow-y: auto;
  margin-bottom: 1rem;
  padding: 1rem;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  min-height: 0;
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

.error-tip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  font-size: 12px;
  color: #f56c6c;
  cursor: pointer;
}

.error-tip:hover {
  text-decoration: underline;
}

.error-tip .el-icon {
  font-size: 14px;
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
