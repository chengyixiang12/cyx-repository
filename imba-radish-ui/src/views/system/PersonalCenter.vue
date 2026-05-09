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
import { getPublicKey } from '@/api/auth'
import { RSAUtil } from '@/utils/rsa'
import { calculateFileMd5 } from '@/utils/filemd5'
import { showMessage } from '@/utils/message'

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
}

// 删除头像
const removeAvatar = () => {
  avatarUrl.value = ''
  showMessage('头像已删除', 'success')
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
}

// 修改密码
const updatePassword = async () => {
  passwordFormRef.value?.validate()
  const publicKey = await getPublicKey(0);
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

// 获取用户信息
const getUserInfo = async () => {
  const userInfo: UserInfoVo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  try {
    const res = await getUserApi(userInfo.id)
    profileForm.value = res

    if (!res.avatar) {
      avatarUrl.value = ''
      return
    }
    const blob = await downloadFileApi(res.avatar)
    avatarUrl.value = URL.createObjectURL(blob)
  } catch (error) {
    ElMessage.error('获取用户信息失败')
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
  background-color: #f5f7fa;
}

.profile-card {
  height: 100%;
}

.card-header {
  font-size: 18px;
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
  border: 2px solid #eaeaea;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.avatar-upload {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 10px;
}

.avatar-tip {
  font-size: 12px;
  color: #999;
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