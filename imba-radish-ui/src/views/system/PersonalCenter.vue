<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>

      <el-row :gutter="30">
        <!-- 左侧头像区域 -->
        <el-col :span="8">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <el-avatar :size="120" :src="avatarUrl" class="user-avatar" />
            </div>

            <div class="avatar-upload">
              <el-upload class="avatar-uploader" :action="''" :upload-before="beforeAvatarUpload" :auto-upload="true"
                :show-file-list="false" :multiple="false" name="multipartFile" :http-request="uploadAvatar"
                ref="uploadRef" enctype="multipart/form-data" :limit="1">
                <el-button type="primary" size="small">
                  <el-icon>
                    <Upload />
                  </el-icon>
                  上传头像
                </el-button>
              </el-upload>

              <el-button type="danger" size="small" @click="removeAvatar"
                :disabled="!avatarUrl || avatarUrl.includes('default')">
                <el-icon>
                  <Delete />
                </el-icon>
                删除头像
              </el-button>
            </div>

            <p class="avatar-tip">支持 JPG、PNG 格式，大小不超过 2MB</p>
          </div>
        </el-col>

        <!-- 右侧信息表单 -->
        <el-col :span="16">
          <el-tabs v-model="activeTab" class="profile-tabs">
            <el-tab-pane label="基本信息" name="basic">
              <el-form ref="basicFormRef" :model="profileForm" :rules="basicRules" label-width="100px"
                class="profile-form">
                <el-form-item label="用户名">
                  <el-input v-model="profileForm.username" disabled />
                </el-form-item>

                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="profileForm.email" placeholder="请输入邮箱地址" />
                </el-form-item>

                <el-form-item label="电话" prop="phone">
                  <el-input v-model="profileForm.phone" placeholder="请输入电话号码" />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" @click="updateProfile">
                    保存信息
                  </el-button>
                  <el-button @click="resetBasicForm(basicFormRef)">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="修改密码" name="password">
              <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px"
                class="password-form">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
                </el-form-item>

                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
                </el-form-item>

                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码"
                    show-password />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" @click="updatePassword">
                    修改密码
                  </el-button>
                  <el-button @click="resetPasswordForm(passwordFormRef)">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Upload, Delete } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import { ElMessage } from 'element-plus'
import { updatePasswordApi, getUserApi, editSelfApi } from '@/api/user'
import { GetUserVo } from '@/types/user'
import { UserInfoVo } from '@/types/login'
import { downloadFileApi, uploadFileApi } from '@/api/file'
import { getPublicKeyApi } from '@/api/auth'
import { RSAUtil } from '@/utils/rsa'
import { calculateFileMd5 } from '@/utils/filemd5'
import { showMessage } from '@/utils/message'
import { useAvatar } from '@/composables/useAvatar'

// 定义表单引用
const basicFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

// 活动标签页
const activeTab = ref('basic')

// 用户信息表单
const profileForm = ref<GetUserVo>({
  id: '',
  username: null,
  nickname: null,
  email: null,
  phone: null,
  deptId: null,
  roleIds: null,
  avatar: null
})

const avatarUrl = ref('');
// 全局头像状态：保存资料后调用 refreshAvatar 让顶栏/首页立即同步
const { refreshAvatar } = useAvatar()

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 基本信息验证规则
const basicRules = reactive<FormRules>({
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度为 2-20 个字符', trigger: 'blur' }
  ]
})

// 密码验证规则
const passwordRules = reactive<FormRules>({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6-20 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6-20 个字符', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value && value === passwordForm.oldPassword) {
          callback(new Error('新密码不能与原密码相同'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 头像上传前检查
const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isJPG = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png'
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
  }

  return isJPG && isLt2M
}

const uploadAvatar = async (options: any) => {
  const { file } = options;
  const fileMd5 = await calculateFileMd5(file)
  const formData = new FormData()
  if (!file) showMessage('请选择头像', 'error')
  formData.append('multipartFile', file)
  formData.append('fileMd5', fileMd5)
  const res = await uploadFileApi(formData)
  profileForm.value.avatar = res.fileId
  // 上传成功后立即显示头像
  avatarUrl.value = URL.createObjectURL(file)
  // 上传接口不再返回 msg 以避免批量上传刷屏，这里单独给头像一个提示
  showMessage('头像上传成功，保存信息后生效', 'success')
}

// 删除头像：仅清空引用与预览，点击「保存信息」后才会真正写库
const removeAvatar = () => {
  avatarUrl.value = ''
  profileForm.value.avatar = null
  showMessage('头像已移除，保存信息后生效', 'success')
}

// 更新用户信息
const updateProfile = async () => {
  await basicFormRef.value?.validate()
  await editSelfApi({
    nickname: profileForm.value.nickname,
    email: profileForm.value.email,
    phone: profileForm.value.phone,
    avatar: profileForm.value.avatar
  })
  // 同步 sessionStorage（登录时写入的那份缓存），否则顶栏要重新登录才能看到新头像
  const cached = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  sessionStorage.setItem('userInfo', JSON.stringify({
    ...cached,
    nickname: profileForm.value.nickname,
    email: profileForm.value.email,
    phone: profileForm.value.phone,
    avatar: profileForm.value.avatar
  }))
  // 刷新全局头像状态，顶栏/首页立即生效
  await refreshAvatar()
  showMessage('保存成功', 'success')
}

// 修改密码
const updatePassword = async () => {
  passwordFormRef.value?.validate()
  const publicKey = await getPublicKeyApi();
  await updatePasswordApi({
    originalPass: RSAUtil.encrypt(passwordForm.oldPassword, publicKey),
    targetPass: RSAUtil.encrypt(passwordForm.newPassword, publicKey)
  })
}

// 重置基本信息表单
const resetBasicForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  getUserInfo()
}

// 重置密码表单
const resetPasswordForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}

// 获取用户信息：头像加载独立处理，避免头像文件丢失时整页初始化被判为失败
const getUserInfo = async () => {
  const userInfo: UserInfoVo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  try {
    const res = await getUserApi(userInfo.id)
    profileForm.value = res
  } catch (error) {
    ElMessage.error('获取用户信息失败')
    return
  }
  await loadAvatar(profileForm.value.avatar)
}

// 加载头像：文件可能已被删除（服务端会返回错误），此处静默降级为默认头像
const loadAvatar = async (fileId?: string | null) => {
  avatarUrl.value = ''
  if (!fileId) return
  try {
    const blob = await downloadFileApi(fileId, true)
    avatarUrl.value = URL.createObjectURL(blob)
  } catch (error) {
    console.warn('头像加载失败，文件可能已被删除:', error)
    // 清掉失效的头像引用，避免保存资料时把悬空的文件 id 再写回数据库
    profileForm.value.avatar = null
  }
}

// 页面加载时获取用户信息
onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  height: 100%;
  background-color: var(--radish-fill);
}

.profile-card {
  height: 100%;
}

.card-header {
  font-size: var(--radish-font-xl);
  font-weight: bold;
}

.avatar-section {
  text-align: center;
  padding: 20px 0;
}

.avatar-wrapper {
  margin-bottom: 20px;
}

.user-avatar {
  border: 2px solid var(--radish-border);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.avatar-upload {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 10px;
}

.avatar-tip {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
  margin: 0;
}

.profile-form,
.password-form {
  max-width: 500px;
  margin-top: 20px;
}

.profile-tabs {
  margin-top: 10px;
}

:deep(.el-tabs__content) {
  padding: 20px 0;
}
</style>