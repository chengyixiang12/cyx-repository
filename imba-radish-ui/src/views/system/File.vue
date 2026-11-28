<template>
  <div class="file-explorer container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>文件管理</span>
        <span class="header-hint">双击进入文件夹或打开文件 · 右键卡片操作</span>
      </div>
      <div class="right-header">
        <el-input v-model="keyword" placeholder="搜索文件名（全局）" clearable class="search-input" @keyup.enter="handleSearch"
          @clear="handleSearch">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button @click="handleAddFolder">
          <el-icon><FolderAdd /></el-icon>
          <span>新建文件夹</span>
        </el-button>
        <el-button type="primary" @click="handleUpload">
          <el-icon><Upload /></el-icon>
          <span>上传文件</span>
        </el-button>
      </div>
    </div>

    <div class="explorer-body">
      <!-- 面包屑 -->
      <div class="breadcrumb-bar">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>
            <span class="crumb" :class="{ active: !currentFolderId && !searching }" @click="jumpTo(null)">全部文件</span>
          </el-breadcrumb-item>
          <el-breadcrumb-item v-for="folder in breadcrumb" :key="folder.id">
            <span class="crumb" :class="{ active: folder.id === currentFolderId && !searching }"
              @click="jumpTo(folder.id)">{{ folder.name }}</span>
          </el-breadcrumb-item>
        </el-breadcrumb>
        <span v-if="searching" class="searching-pill">全局搜索「{{ searchedKeyword }}」· 匹配时忽略当前目录</span>
      </div>

      <!-- 内容网格 -->
      <div class="grid-wrap" v-loading="loading" @click="closeMenu">
        <div v-if="folders.length === 0 && files.length === 0 && !loading" class="empty-state">
          <el-icon :size="48" class="empty-icon"><FolderOpened /></el-icon>
          <div class="empty-text">{{ searching ? '没有匹配的文件' : '此文件夹是空的' }}</div>
          <div class="empty-sub">上传文件或新建文件夹开始整理</div>
        </div>

        <div class="card-grid">
          <!-- 文件夹卡片 -->
          <div v-for="folder in folders" :key="'f-' + folder.id" class="item-card folder"
            :class="{ selected: selectedKey === 'f-' + folder.id }" @click="select('f-' + folder.id)"
            @dblclick="jumpTo(folder.id)" @contextmenu.prevent="openMenu($event, 'folder', folder)">
            <div class="thumb-wrap">
              <el-icon :size="52" class="folder-icon"><Folder /></el-icon>
            </div>
            <div class="item-name" :title="folder.name">{{ folder.name }}</div>
            <div class="item-sub">{{ folder.fileCount ?? 0 }} 个文件</div>
          </div>

          <!-- 文件卡片 -->
          <div v-for="file in files" :key="'p-' + file.id" class="item-card file"
            :class="{ selected: selectedKey === 'p-' + file.id }" @click="select('p-' + file.id)"
            @dblclick="openFile(file)" @contextmenu.prevent="openMenu($event, 'file', file)">
            <div class="thumb-wrap">
              <img v-if="thumbMap[file.id]" :src="thumbMap[file.id]" class="thumb" :alt="file.originalName"
                @error="onThumbError(file.id)" />
              <el-icon v-else :size="40" class="type-icon" :class="typeClass(file)">
                <component :is="typeIcon(file)" />
              </el-icon>
            </div>
            <div class="item-name" :title="file.originalName">{{ file.originalName }}</div>
            <div class="item-sub">{{ formatFileSize(file.fileSize) }}</div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pager" v-if="total > 0">
        <el-pagination :current-page="page" :page-size="pageSize" :total="total"
          layout="total, sizes, prev, pager, next" :page-sizes="[20, 50, 100]" @current-change="handlePageChange"
          @size-change="handleSizeChange" />
      </div>
    </div>

    <!-- 右键菜单（teleport 到 body，避免被卡片 overflow 裁剪） -->
    <teleport to="body">
      <div v-if="menu.visible" class="context-menu" :style="{ left: menu.x + 'px', top: menu.y + 'px' }"
        @click.stop @contextmenu.prevent>
        <template v-if="menu.type === 'folder'">
          <div class="menu-item" @click="menuOpenFolder">
            <el-icon><FolderOpened /></el-icon><span>打开</span>
          </div>
          <div class="menu-item" @click="menuRenameFolder">
            <el-icon><EditPen /></el-icon><span>重命名</span>
          </div>
          <div class="menu-item danger" @click="menuDeleteFolder">
            <el-icon><Delete /></el-icon><span>删除文件夹</span>
          </div>
        </template>
        <template v-else-if="menu.file">
          <div v-if="isPreviewable(menu.file)" class="menu-item" @click="menuPreview">
            <el-icon><View /></el-icon><span>预览</span>
          </div>
          <div class="menu-item" @click="menuDownload">
            <el-icon><Download /></el-icon><span>下载</span>
          </div>
          <div class="menu-item" @click="menuDetail">
            <el-icon><InfoFilled /></el-icon><span>查看详情</span>
          </div>
          <div class="menu-item" @click="menuMoveFile">
            <el-icon><Position /></el-icon><span>移动到…</span>
          </div>
          <div class="menu-item danger" @click="menuDeleteFile">
            <el-icon><Delete /></el-icon><span>删除</span>
          </div>
        </template>
      </div>
    </teleport>

    <!-- 文件详情抽屉 -->
    <el-drawer v-model="detail.visible" title="文件详情" size="440px">
      <el-descriptions v-if="detail.data" :column="1" border size="small">
        <el-descriptions-item label="文件名">
          <span class="break-all">{{ detail.data.originalName }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="类型">{{ suffixText(detail.data.fileSuffix) }}</el-descriptions-item>
        <el-descriptions-item label="大小">{{ formatFileSize(detail.data.fileSize) }}</el-descriptions-item>
        <el-descriptions-item label="存储位置">{{ detail.data.locationName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="上传人">{{ detail.data.createBy || '—' }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ detail.data.createTime || '—' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <!-- 移动文件弹窗 -->
    <el-dialog v-model="move.visible" title="移动文件" width="420px" append-to-body>
      <div class="move-hint">「{{ move.fileName }}」移动到：</div>
      <el-tree v-loading="move.loading" ref="moveTreeRef" :data="move.tree" node-key="id"
        :props="{ label: 'name', children: 'children' }" highlight-current default-expand-all
        :expand-on-click-node="false" class="move-tree" @node-click="(node: FolderTreeNode) => move.target = node.id" />
      <div v-if="!move.loading && move.tree.length === 0" class="move-empty">暂无文件夹，请先新建</div>
      <template #footer>
        <el-button @click="move.visible = false">取消</el-button>
        <el-button type="primary" :disabled="!move.target" @click="confirmMove">移动</el-button>
      </template>
    </el-dialog>

    <!-- 预览弹窗 -->
    <el-dialog v-model="preview.visible" :title="preview.name" width="720px" destroy-on-close append-to-body>
      <div class="preview-body">
        <img v-if="preview.type === 'image'" :src="preview.url" class="preview-media" :alt="preview.name" />
        <video v-else-if="preview.type === 'video'" :src="preview.url" class="preview-media" controls
          preload="metadata"></video>
        <audio v-else-if="preview.type === 'audio'" :src="preview.url" controls class="preview-audio"></audio>
        <iframe v-else-if="preview.type === 'pdf'" :src="preview.url" class="preview-pdf"></iframe>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { ElTree } from 'element-plus'
import {
  Search, Upload, Folder, FolderAdd, FolderOpened, Delete, EditPen, View, Download, InfoFilled,
  Document, VideoPlay, Picture, Headset, Tickets, Box, Position
} from '@element-plus/icons-vue'
import { showMessage } from '@/utils/message'
import { download } from '@/utils/download'
import {
  addFolderApi, deleteFileApi, deleteFolderApi, downloadFileApi, getFileListApi,
  getFileUrlApi, getFolderTreeApi, moveFileApi, renameFolderApi
} from '@/api/file'
import { setUploadFolderId, useUploadTasks } from '@/composables/useUploadTasks'
import type { FilesVo, FolderTreeNode, FolderVo } from '@/types/file'

const PAGE_SIZE_DEFAULT = 20

const { openPicker, successVersion } = useUploadTasks()

const loading = ref(false)
const folders = ref<FolderVo[]>([])
const files = ref<FilesVo[]>([])
const total = ref(0)
const breadcrumb = ref<FolderVo[]>([])

const currentFolderId = ref<string | null>(null)
const keyword = ref('')
const searchedKeyword = ref('')
const searching = computed(() => searchedKeyword.value !== '')
const page = ref(1)
const pageSize = ref(PAGE_SIZE_DEFAULT)

const selectedKey = ref('')
const thumbMap = reactive<Record<string, string>>({})

const menu = reactive({
  visible: false, x: 0, y: 0,
  type: 'file' as 'folder' | 'file',
  folder: null as FolderVo | null,
  file: null as FilesVo | null
})

const detail = reactive({ visible: false, data: null as FilesVo | null })
const preview = reactive({
  visible: false, type: 'image' as 'image' | 'video' | 'audio' | 'pdf',
  url: '', name: ''
})

// 移动文件：树形选择目标文件夹
const moveTreeRef = ref<InstanceType<typeof ElTree>>()
const move = reactive({
  visible: false, loading: false,
  fileId: '', fileName: '',
  /** 已选目标文件夹 id */
  target: '',
  tree: [] as FolderTreeNode[]
})

// 上传任务面板有新成功任务时自动刷新当前目录
watch(successVersion, () => load())

// ==================== 预览类型判断 ====================

const IMAGE_SUFFIX = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg']
const VIDEO_SUFFIX = ['mp4', 'webm', 'ogv']
const AUDIO_SUFFIX = ['mp3', 'wav', 'ogg', 'flac', 'm4a']

const suffixOf = (file: { fileSuffix?: string; originalName: string }) => {
  const raw = file.fileSuffix || ''
  if (raw) return raw.replace(/^\./, '').toLowerCase()
  const dot = file.originalName.lastIndexOf('.')
  return dot >= 0 ? file.originalName.slice(dot + 1).toLowerCase() : ''
}

const isPreviewable = (file: FilesVo) => {
  const suffix = suffixOf(file)
  return IMAGE_SUFFIX.includes(suffix) || VIDEO_SUFFIX.includes(suffix)
    || AUDIO_SUFFIX.includes(suffix) || suffix === 'pdf'
}

const previewTypeOf = (file: FilesVo): 'image' | 'video' | 'audio' | 'pdf' => {
  const suffix = suffixOf(file)
  if (IMAGE_SUFFIX.includes(suffix)) return 'image'
  if (VIDEO_SUFFIX.includes(suffix)) return 'video'
  if (AUDIO_SUFFIX.includes(suffix)) return 'audio'
  return 'pdf'
}

const typeIcon = (file: FilesVo) => {
  const suffix = suffixOf(file)
  if (IMAGE_SUFFIX.includes(suffix)) return Picture
  if (VIDEO_SUFFIX.includes(suffix)) return VideoPlay
  if (AUDIO_SUFFIX.includes(suffix)) return Headset
  if (suffix === 'pdf') return Tickets
  if (['zip', 'rar', '7z', 'tar', 'gz'].includes(suffix)) return Box
  return Document
}

const typeClass = (file: FilesVo) => {
  const suffix = suffixOf(file)
  if (IMAGE_SUFFIX.includes(suffix)) return 'is-image'
  if (VIDEO_SUFFIX.includes(suffix)) return 'is-video'
  if (AUDIO_SUFFIX.includes(suffix)) return 'is-audio'
  return ''
}

const suffixText = (suffix?: string) => {
  if (!suffix) return '未知'
  return suffix.startsWith('.') ? suffix.slice(1).toUpperCase() : suffix.toUpperCase()
}

// ==================== 数据加载 ====================

const load = async () => {
  loading.value = true
  try {
    const res = await getFileListApi({
      folderId: currentFolderId.value,
      keyword: searchedKeyword.value || undefined,
      pageNum: page.value,
      pageSize: pageSize.value
    })
    folders.value = res.folders || []
    files.value = res.files?.records || []
    total.value = res.files?.total || 0
    breadcrumb.value = res.breadcrumb || []
    selectedKey.value = ''
    // 图片类文件懒加载缩略图（签名 URL 有时效，只对图片取）
    files.value.filter(file => IMAGE_SUFFIX.includes(suffixOf(file)) && !thumbMap[file.id])
      .forEach(file => loadThumb(file.id))
  } finally {
    loading.value = false
  }
}

const loadThumb = async (id: string) => {
  try {
    thumbMap[id] = (await getFileUrlApi(id, '1')).url
  } catch {
    // 缩略图失败静默降级为类型图标
  }
}

const onThumbError = (id: string) => {
  delete thumbMap[id]
}

const jumpTo = (folderId: string | null) => {
  if (searching.value || currentFolderId.value === folderId) return
  currentFolderId.value = folderId
  page.value = 1
  load()
}

const select = (key: string) => {
  selectedKey.value = key
  closeMenu()
}

const handleSearch = () => {
  searchedKeyword.value = keyword.value.trim()
  page.value = 1
  load()
}

const handlePageChange = (val: number) => {
  page.value = val
  load()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  page.value = 1
  load()
}

const handleUpload = () => {
  // 入队瞬间会把当前目录快照到每个任务
  setUploadFolderId(currentFolderId.value)
  openPicker()
}

const handleAddFolder = async () => {
  const name = await promptText('新建文件夹', '请输入文件夹名称')
  if (!name) return
  await addFolderApi(name, currentFolderId.value)
  load()
}

// ==================== 双击行为 ====================

const openFile = (file: FilesVo) => {
  if (isPreviewable(file)) {
    doPreview(file)
  } else {
    showMessage('该格式浏览器不支持预览，可右键下载', 'info')
  }
}

// ==================== 右键菜单 ====================

const openMenu = (event: MouseEvent, type: 'folder' | 'file', data: FolderVo | FilesVo) => {
  selectedKey.value = (type === 'folder' ? 'f-' : 'p-') + data.id
  const menuWidth = 160
  const menuHeight = type === 'folder' ? 130 : 215
  menu.type = type
  menu.folder = type === 'folder' ? data as FolderVo : null
  menu.file = type === 'file' ? data as FilesVo : null
  menu.x = Math.min(event.clientX, window.innerWidth - menuWidth - 8)
  menu.y = Math.min(event.clientY, window.innerHeight - menuHeight - 8)
  menu.visible = true
}

const closeMenu = () => {
  menu.visible = false
}

const menuOpenFolder = () => {
  const folder = menu.folder
  closeMenu()
  if (folder) jumpTo(folder.id)
}

const menuRenameFolder = async () => {
  const folder = menu.folder
  closeMenu()
  if (!folder) return
  const name = await promptText('重命名文件夹', '请输入新名称', folder.name)
  if (!name || name === folder.name) return
  await renameFolderApi(folder.id, name)
  load()
}

const menuDeleteFolder = async () => {
  const folder = menu.folder
  closeMenu()
  if (!folder) return
  await confirmDanger(`删除文件夹「${folder.name}」？`, '非空文件夹会被拒绝删除')
  await deleteFolderApi(folder.id)
  load()
}

const menuPreview = () => {
  const file = menu.file
  closeMenu()
  if (file) doPreview(file)
}

const menuDownload = () => {
  const file = menu.file
  closeMenu()
  if (file) downloadFile(file)
}

const menuDetail = () => {
  const file = menu.file
  closeMenu()
  if (!file) return
  detail.data = file
  detail.visible = true
}

const menuDeleteFile = async () => {
  const file = menu.file
  closeMenu()
  if (!file) return
  await confirmDanger(`删除文件「${file.originalName}」？`, '删除后不可恢复')
  await deleteFileApi(file.id)
  delete thumbMap[file.id]
  load()
}

const menuMoveFile = async () => {
  const file = menu.file
  closeMenu()
  if (!file) return
  move.fileId = file.id
  move.fileName = file.originalName
  move.target = file.folderId || ''
  move.tree = []
  move.visible = true
  move.loading = true
  try {
    move.tree = await getFolderTreeApi()
    // 预选文件当前所在文件夹，视觉上高亮
    if (move.target) {
      await nextTick()
      moveTreeRef.value?.setCurrentKey(move.target)
    }
  } catch {
    move.tree = []
  } finally {
    move.loading = false
  }
}

const confirmMove = async () => {
  if (!move.target) return
  // 原地移动等校验交给后端，前端不重复提示（拦截器会展示后端 msg）
  await moveFileApi(move.fileId, move.target)
  move.visible = false
  load()
}

// ==================== 预览与下载 ====================

const doPreview = async (file: FilesVo) => {
  preview.name = file.originalName
  preview.type = previewTypeOf(file)
  preview.visible = true
  try {
    // isInline=1：后端带 inline disposition，浏览器直接渲染而不是触发下载
    preview.url = (await getFileUrlApi(file.id, '1')).url
  } catch {
    // 拦截器已展示后端错误提示，这里只收起预览窗
    preview.visible = false
  }
}

const downloadFile = async (file: FilesVo) => {
  showMessage('正在下载，请等待', 'success')
  const MAX_SIZE = 20 * 1024 * 1024
  if (file.fileSize > MAX_SIZE) {
    const fileUrl = (await getFileUrlApi(file.id, '0')).url
    const link = document.createElement('a')
    link.href = fileUrl
    link.download = file.originalName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } else {
    const blob = await downloadFileApi(file.id)
    download(blob, file.originalName)
  }
}

// ==================== 通用交互工具 ====================

const promptText = (title: string, placeholder: string, inputValue = ''): Promise<string | null> => {
  return ElMessageBox.prompt(placeholder, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue,
    inputPattern: /\S+/,
    inputErrorMessage: '名称不能为空'
  }).then(({ value }) => value).catch(() => null)
}

const confirmDanger = async (message: string, tip: string): Promise<void> => {
  await ElMessageBox.confirm(message, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).catch(() => Promise.reject(new Error('cancelled')))
}

// ==================== 工具与生命周期 ====================

const formatFileSize = (size?: number): string => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.min(Math.floor(Math.log(size) / Math.log(1024)), units.length - 1)
  return parseFloat((size / Math.pow(1024, i)).toFixed(2)) + ' ' + units[i]
}

const onDocClick = () => closeMenu()
const onDocScroll = () => closeMenu()

onMounted(() => {
  load()
  document.addEventListener('click', onDocClick)
  document.addEventListener('scroll', onDocScroll, true)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocClick)
  document.removeEventListener('scroll', onDocScroll, true)
})
</script>

<style scoped>
.header-hint {
  font-size: var(--radish-font-xs);
  font-weight: 400;
  color: var(--radish-text-secondary);
}

.search-input {
  width: 220px;
}

.explorer-body {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 0 16px 16px;
  gap: var(--radish-space-3);
  /* 与顶部「文件管理」header 保持间距 */
  margin-top: var(--radish-space-3);
}

.breadcrumb-bar {
  display: flex;
  align-items: center;
  gap: var(--radish-space-3);
  padding: 10px 14px;
  background: var(--radish-bg-card);
  border-radius: var(--radish-radius-lg);
  box-shadow: var(--radish-shadow-card);
}

.crumb {
  cursor: pointer;
  font-size: var(--radish-font-sm);
  color: var(--radish-text-regular);
}

.crumb:hover {
  color: var(--radish-primary);
}

.crumb.active {
  color: var(--radish-text-primary);
  font-weight: 600;
}

.searching-pill {
  font-size: var(--radish-font-xs);
  color: var(--radish-primary);
  background: var(--radish-primary-light-9);
  padding: 2px 10px;
  border-radius: var(--radish-radius-round);
}

.grid-wrap {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  background: var(--radish-bg-card);
  border-radius: var(--radish-radius-lg);
  box-shadow: var(--radish-shadow-card);
  padding: var(--radish-space-4);
}

.card-grid {
  display: flex;
  flex-wrap: wrap;
  gap: var(--radish-space-3);
}

.item-card {
  width: 128px;
  padding: var(--radish-space-3) var(--radish-space-2);
  border-radius: var(--radish-radius);
  border: 1px solid transparent;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--radish-space-1);
  cursor: pointer;
  transition: background var(--radish-transition), border-color var(--radish-transition);
  user-select: none;
}

.item-card:hover {
  background: var(--radish-fill);
}

.item-card.selected {
  background: var(--radish-primary-light-9);
  border-color: var(--radish-primary-light-7);
}

.folder-icon {
  color: var(--radish-primary);
}

.thumb-wrap {
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.thumb {
  max-width: 72px;
  max-height: 72px;
  border-radius: var(--radish-radius-sm);
  object-fit: cover;
}

.type-icon {
  color: var(--radish-text-secondary);
}

.type-icon.is-image {
  color: var(--radish-success);
}

.type-icon.is-video {
  color: var(--radish-primary);
}

.type-icon.is-audio {
  color: var(--radish-warning);
}

.item-name {
  width: 100%;
  text-align: center;
  font-size: var(--radish-font-sm);
  color: var(--radish-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-sub {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
}

.empty-state {
  height: 100%;
  min-height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--radish-space-2);
}

.empty-icon {
  color: var(--radish-text-placeholder);
}

.empty-text {
  font-size: var(--radish-font-base);
  color: var(--radish-text-regular);
}

.empty-sub {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
}

.pager {
  display: flex;
  justify-content: flex-end;
}

/* 右键菜单：teleport 到 body，样式不能走 scoped 作用域，需挂在全局类上 */
.context-menu {
  position: fixed;
  z-index: 3000;
  width: 160px;
  padding: var(--radish-space-1);
  background: var(--radish-bg-card);
  border: 1px solid var(--radish-border-light);
  border-radius: var(--radish-radius);
  box-shadow: var(--radish-shadow-pop);
}

.move-hint {
  margin-bottom: var(--radish-space-2);
  color: var(--radish-text-secondary);
  word-break: break-all;
}

.move-tree {
  max-height: 320px;
  overflow-y: auto;
  border: 1px solid var(--radish-border-light);
  border-radius: var(--radish-radius);
  padding: var(--radish-space-1);
}

.move-empty {
  padding: var(--radish-space-4) 0;
  text-align: center;
  color: var(--radish-text-secondary);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: var(--radish-space-2);
  padding: 8px 12px;
  border-radius: var(--radish-radius-sm);
  font-size: var(--radish-font-sm);
  color: var(--radish-text-primary);
  cursor: pointer;
}

.menu-item:hover {
  background: var(--radish-primary-light-9);
  color: var(--radish-primary);
}

.menu-item.danger {
  color: var(--radish-danger);
}

.menu-item.danger:hover {
  background: var(--radish-danger-light);
  color: var(--radish-danger);
}

/* 预览 */
.preview-body {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  background: var(--radish-fill);
  border-radius: var(--radish-radius);
  overflow: hidden;
}

.preview-media {
  max-width: 100%;
  max-height: 60vh;
}

.preview-audio {
  width: 100%;
}

.preview-pdf {
  width: 100%;
  height: 60vh;
  border: none;
  background: var(--radish-bg-card);
}

.break-all {
  word-break: break-all;
}
</style>
