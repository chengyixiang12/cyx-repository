<template>
  <div class="page-container">
    <!-- 消息展示区 -->
    <div class="chat-box" ref="chatBox" @scroll="handleScroll">
      <!-- 历史消息加载骨架 -->
      <MessageSkeleton :loading="initialLoading" :count="messages.length" />
      <!-- 历史消息懒加载入口 -->
      <div v-if="hasMore" class="history-hint" @click="loadOlder">
        <el-icon v-if="loadingMore" class="is-loading"><Loading /></el-icon>
        <span>{{ loadingMore ? '正在加载更早的消息...' : '上滑或点击加载更早的消息' }}</span>
      </div>

      <!-- 空状态 -->
      <div v-if="messages.length === 0 && !initialLoading" class="empty-state">
        <div class="empty-logo">
          <el-icon><MagicStick /></el-icon>
        </div>
        <h2 class="empty-title">有什么可以帮你？</h2>
        <p class="empty-desc">在下方输入问题，按 Enter 发送，开始与 AI 对话</p>
      </div>

      <div v-for="msg in messages" :key="msg.id" class="message-row" :class="msg.role">
        <!-- 头像 -->
        <div class="avatar" :class="msg.role">
          <el-icon v-if="msg.role === 'assistant'"><MagicStick /></el-icon>
          <el-icon v-else><User /></el-icon>
        </div>

        <div class="message-body">
          <div v-if="msg.status === 'error'" class="error-tip" @click="retryMessage(msg)">
            <el-icon><WarningFilled /></el-icon>
            <span>发送失败，点击重试</span>
          </div>
          <!-- AI 打字中 -->
          <div v-else-if="msg.role === 'assistant' && msg.status === 'sending'" class="typing-dots">
            <span></span><span></span><span></span>
          </div>
          <!-- 用户消息: 纯文本 -->
          <div v-else-if="msg.role === 'user'" class="message-bubble user-bubble">{{ msg.content }}</div>
          <!-- AI 消息: Markdown -->
          <div v-else class="message-bubble markdown-body">
            <VueMarkdownIt :source="msg.content" />
          </div>
        </div>
      </div>
    </div>

    <!-- 输入框和发送按钮 -->
    <div class="input-wrapper">
      <el-input v-model="input" type="textarea" placeholder="请输入消息，按 Enter 发送" @keyup.enter="sendMessage"
        :autosize="{ minRows: 3, maxRows: 6 }" class="custom-textarea" :disabled="isLoading" />
      <el-button type="primary" class="send-btn" @click="sendMessage" :disabled="isLoading || !input.trim()"
        :title="isLoading ? '发送中' : '发送'">
        <el-icon><Promotion /></el-icon>
      </el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, nextTick, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { WarningFilled, MagicStick, User, Promotion, Loading } from '@element-plus/icons-vue'
import { getWebSocketInstance } from '@/utils/websocket'
import VueMarkdownIt from 'vue3-markdown-it'
import { saveDialogueApi, getDialogueDetailsApi } from '@/api/dialogueHistory'
import { SaveDialogueRequest, DialogueDetailsVo } from '@/types/dialogueHistory'
import MessageSkeleton from '@/components/skeleton/MessageSkeleton.vue'
import { WebsocketMessage } from '@/utils/websocketManager'

interface Message {
  id: string
  role: 'user' | 'assistant'
  content: string
  status?: 'sending' | 'error' | 'done'
}

const route = useRoute()
const input = ref('')
const messages = ref<Message[]>([])
const chatBox = ref<HTMLElement>()
const wsInstance = getWebSocketInstance()
const dialogue = ref<SaveDialogueRequest>({
  title: null
})
// 对话id: 新建时由后端返回字符串, 历史对话来自路由参数
const dialogueId = ref<string | number | null>(null)
const isLoading = ref(false)

// 历史消息懒加载状态
const HISTORY_PAGE_SIZE = 20
const historyPageNum = ref(1)
const hasMore = ref(false)
const loadingMore = ref(false)
const initialLoading = ref(false)

// 生成唯一消息ID
const generateMessageId = () => {
  return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}

const scrollToBottom = (smooth = true) => {
  nextTick(() => {
    chatBox.value?.scrollTo({ top: chatBox.value.scrollHeight, behavior: smooth ? 'smooth' : 'auto' })
  })
}

// 重发消息
const retryMessage = async (msg: Message) => {
  if (msg.role !== 'user' || msg.status !== 'error') return

  // 新一轮回答: 重置流式渲染目标
  streamTarget = null
  streamBuffer = ''

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

  // 新一轮回答: 重置流式渲染目标
  streamTarget = null
  streamBuffer = ''

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

// ---------- AI 流式回复渲染: 缓冲 + 节流 ----------
// 后端按 token 推送, 若每个分片都全量重渲染 Markdown, 长回答会越推越卡(O(n²))。
// 这里将分片攒入缓冲, 每 60ms 最多触发一次响应式更新; 滚动也改为非平滑跟随。
let streamBuffer = ''
let streamTarget: Message | null = null
let flushTimer: ReturnType<typeof setTimeout> | null = null
const STREAM_FLUSH_INTERVAL = 60

const flushStream = () => {
  flushTimer = null
  if (!streamBuffer || !streamTarget) return
  streamTarget.content += streamBuffer
  streamBuffer = ''
  scrollToBottom(false)
}

const scheduleFlush = () => {
  if (flushTimer === null) {
    flushTimer = setTimeout(flushStream, STREAM_FLUSH_INTERVAL)
  }
}

wsInstance.aiAnwser = (message: WebsocketMessage) => {
  // 每轮回答的第一个分片: 接管"正在输入"占位或新建消息, 后续分片都追加到同一目标
  if (!streamTarget) {
    const last = messages.value[messages.value.length - 1]
    if (isLoading.value && last && last.role === 'assistant' && last.content === 'AI 正在输入中...') {
      last.content = ''
      last.status = 'done'
      streamTarget = last
    } else {
      streamTarget = { id: generateMessageId(), role: 'assistant', content: '', status: 'done' }
      messages.value.push(streamTarget)
    }
  }
  isLoading.value = false

  streamBuffer += message.answer
  scheduleFlush()
}

// ---------- 历史对话加载 ----------
const mapDetailsToMessages = (list: DialogueDetailsVo[]): Message[] => {
  return list.map(item => ({
    id: item.id || generateMessageId(),
    role: item.tag === 1 ? 'user' : 'assistant',
    content: item.content,
    status: 'done'
  }))
}

// 加载历史对话: 先取最新一页并滚到底部
const loadHistory = async (id: string) => {
  initialLoading.value = true
  historyPageNum.value = 1
  hasMore.value = false
  streamTarget = null
  streamBuffer = ''
  try {
    const res = await getDialogueDetailsApi({ dialogueId: id, pageNum: 1, pageSize: HISTORY_PAGE_SIZE })
    messages.value = mapDetailsToMessages(res.records || [])
    hasMore.value = messages.value.length < (Number(res.total) || 0)
    await nextTick()
    chatBox.value?.scrollTo({ top: chatBox.value.scrollHeight })
  } catch (e) {
    ElMessage.error('加载聊天记录失败')
  } finally {
    initialLoading.value = false
  }
}

// 向上懒加载更早的消息: 插入顶部并保持当前阅读位置
const loadOlder = async () => {
  if (!hasMore.value || loadingMore.value || initialLoading.value) return
  const id = route.query.dialogueId
  if (typeof id !== 'string' || !id) return

  loadingMore.value = true
  const box = chatBox.value
  const prevHeight = box?.scrollHeight ?? 0
  const prevTop = box?.scrollTop ?? 0
  historyPageNum.value += 1
  try {
    const res = await getDialogueDetailsApi({ dialogueId: id, pageNum: historyPageNum.value, pageSize: HISTORY_PAGE_SIZE })
    const older = mapDetailsToMessages(res.records || [])
    messages.value = [...older, ...messages.value]
    hasMore.value = messages.value.length < (Number(res.total) || 0)
    await nextTick()
    // 保持滚动位置: 新内容高度补偿到 scrollTop, 避免视口跳动
    if (box) {
      box.scrollTop = box.scrollHeight - prevHeight + prevTop
    }
  } catch (e) {
    historyPageNum.value -= 1
    ElMessage.error('加载更早的消息失败')
  } finally {
    loadingMore.value = false
  }
}

// 滚动到顶部附近时自动加载更早的消息
const handleScroll = () => {
  if (!hasMore.value || loadingMore.value) return
  const box = chatBox.value
  if (box && box.scrollTop < 40) {
    loadOlder()
  }
}

// 根据路由参数初始化: 有 dialogueId 则加载历史对话, 否则视为新对话
const initFromRoute = () => {
  const id = route.query.dialogueId
  if (typeof id === 'string' && id) {
    dialogueId.value = id
    loadHistory(id)
  } else {
    dialogueId.value = null
    messages.value = []
    hasMore.value = false
  }
}

onMounted(initFromRoute)

// 已在对话页时从历史列表再次点击其他对话(仅 query 变化), 需要重新加载
watch(() => route.query.dialogueId, (val) => {
  if (typeof val === 'string' && val) {
    dialogueId.value = val
    loadHistory(val)
  } else {
    // 切回"新对话"菜单: 重置为新会话
    dialogueId.value = null
    messages.value = []
    hasMore.value = false
  }
})
</script>

<style scoped>
@import 'github-markdown-css/github-markdown.css';

.page-container {
  display: flex;
  flex-direction: column;
  padding: 12px 16px 16px;
  min-height: calc(100vh - 160px);
  max-height: calc(100vh - 160px);
  box-sizing: border-box;
  background-color: var(--radish-bg-page);
  overflow: hidden;
}

/* ---------- 消息展示区 ---------- */
.chat-box {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 16px 4px;
}

/* 空状态: 品牌化欢迎区 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 300px;
  user-select: none;
}

.empty-logo {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
  background: linear-gradient(135deg, var(--radish-primary), var(--radish-primary-light-3));
  box-shadow: 0 8px 20px rgba(79, 110, 242, 0.35);
  margin-bottom: 20px;
}

.empty-title {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: var(--radish-text-primary);
}

.empty-desc {
  margin: 0;
  font-size: 13px;
  color: var(--radish-text-secondary);
}

/* 历史消息懒加载提示 */
.history-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 16px;
  padding: 6px 0;
  font-size: 12px;
  color: var(--radish-text-secondary);
  cursor: pointer;
  user-select: none;
}

.history-hint:hover {
  color: var(--radish-primary);
}

/* ---------- 消息行: 头像 + 气泡 ---------- */
.message-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 20px;
}

.message-row.user {
  flex-direction: row-reverse;
}

.avatar {
  flex-shrink: 0;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  margin-top: 2px;
}

.avatar.assistant {
  color: #fff;
  background: linear-gradient(135deg, var(--radish-primary), var(--radish-primary-light-3));
  box-shadow: 0 2px 6px rgba(79, 110, 242, 0.3);
}

.avatar.user {
  color: var(--radish-primary);
  background: var(--radish-primary-light-8);
}

.message-body {
  max-width: 78%;
  min-width: 0;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  word-break: break-word;
}

/* 用户消息: 主色实底气泡 */
.user-bubble {
  background: var(--radish-primary);
  color: #fff;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  border-bottom-right-radius: 4px;
  box-shadow: 0 2px 6px rgba(79, 110, 242, 0.25);
}

/* AI 消息: 白卡片 */
.message-row.assistant .message-bubble {
  background: var(--radish-bg-card);
  border: 1px solid var(--radish-border);
  border-bottom-left-radius: 4px;
  padding: 14px 18px;
  box-shadow: var(--radish-shadow-card);
}

/* 气泡内的表格/代码块与边框保持间距 */
.message-row.assistant .message-bubble :deep(.markdown-body table),
.message-row.assistant .message-bubble :deep(.markdown-body pre) {
  margin-left: 4px;
  margin-right: 4px;
}

/* AI 打字中: 三点跳动 */
.typing-dots {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 14px 16px;
  background: var(--radish-bg-card);
  border: 1px solid var(--radish-border);
  border-radius: 12px;
  border-bottom-left-radius: 4px;
}

.typing-dots span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--radish-primary-light-5);
  animation: typing-bounce 1.2s infinite ease-in-out;
}

.typing-dots span:nth-child(2) {
  animation-delay: 0.15s;
}

.typing-dots span:nth-child(3) {
  animation-delay: 0.3s;
}

@keyframes typing-bounce {

  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.5;
  }

  30% {
    transform: translateY(-5px);
    opacity: 1;
  }
}

/* 发送失败提示 */
.error-tip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 2px;
  font-size: 12px;
  color: var(--radish-danger);
  cursor: pointer;
}

.error-tip:hover {
  text-decoration: underline;
}

.error-tip .el-icon {
  font-size: 14px;
}

/* ---------- 输入区: 一体化卡片 ---------- */
.input-wrapper {
  position: relative;
  background: var(--radish-bg-card);
  border: 1px solid var(--radish-border);
  border-radius: var(--radish-radius-lg);
  padding: 10px 56px 10px 14px;
  box-shadow: var(--radish-shadow-card);
  transition: border-color 0.2s, box-shadow 0.2s;
}

.input-wrapper:focus-within {
  border-color: var(--radish-primary);
  box-shadow: 0 0 0 3px var(--radish-primary-light-8);
}

.custom-textarea :deep(.el-textarea__inner) {
  padding: 4px;
  resize: none;
  border: none;
  box-shadow: none;
  background: transparent;
  font-size: 14px;
  line-height: 1.6;
}

.send-btn {
  position: absolute;
  right: 10px;
  bottom: 10px;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  font-size: 17px;
  padding: 0;
}

/* ---------- Markdown 定制 ---------- */
:deep(.markdown-body) {
  font-size: 14px;
  line-height: 1.7;
  color: var(--radish-text-primary);
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
  color: var(--radish-text-primary);
}

:deep(.markdown-body h1) {
  font-size: 19px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--radish-border-light);
}

:deep(.markdown-body h2) {
  font-size: 17px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--radish-border-light);
}

:deep(.markdown-body h3) {
  font-size: 15px;
}

:deep(.markdown-body p) {
  margin-top: 8px;
  margin-bottom: 8px;
}

:deep(.markdown-body ul),
:deep(.markdown-body ol) {
  margin-top: 8px;
  margin-bottom: 8px;
  padding-left: 22px;
}

:deep(.markdown-body li) {
  margin-top: 4px;
  margin-bottom: 4px;
}

:deep(.markdown-body code) {
  padding: 2px 5px;
  font-size: 0.9em;
  background-color: var(--radish-fill);
  border: 1px solid var(--radish-border-light);
  border-radius: 4px;
  color: var(--radish-primary-dark-2);
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

:deep(.markdown-body pre) {
  padding: 12px 14px;
  margin-top: 8px;
  margin-bottom: 8px;
  background-color: #282c34;
  border-radius: var(--radish-radius);
  overflow-x: auto;
  font-size: 0.9em;
}

:deep(.markdown-body pre code) {
  padding: 0;
  background-color: transparent;
  border: none;
  border-radius: 0;
  color: #abb2bf;
}

:deep(.markdown-body blockquote) {
  padding: 10px 16px;
  margin-top: 8px;
  margin-bottom: 8px;
  border-left: 3px solid var(--radish-primary-light-5);
  background-color: var(--radish-fill);
  border-radius: 0 var(--radish-radius-sm) var(--radish-radius-sm) 0;
  color: var(--radish-text-regular);
}

:deep(.markdown-body table) {
  border-collapse: collapse;
  margin-top: 8px;
  margin-bottom: 8px;
  width: 100%;
  font-size: 0.9em;
}

:deep(.markdown-body th) {
  background-color: var(--radish-fill);
  border: 1px solid var(--radish-border);
  padding: 6px 13px;
  text-align: left;
  font-weight: 600;
}

:deep(.markdown-body td) {
  border: 1px solid var(--radish-border);
  padding: 6px 13px;
}

:deep(.markdown-body tr:nth-child(even)) {
  background-color: var(--radish-fill);
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .page-container {
    padding: 8px;
  }

  .message-body {
    max-width: 88%;
  }

  .message-bubble {
    padding: 9px 12px;
  }

  .input-wrapper {
    padding: 8px 48px 8px 10px;
  }

  .send-btn {
    right: 8px;
    bottom: 8px;
    width: 32px;
    height: 32px;
  }
}
</style>
