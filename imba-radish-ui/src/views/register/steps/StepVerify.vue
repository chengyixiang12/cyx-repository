<template>
  <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
    <el-form-item label="验证码" prop="verificationCode">
      <el-input v-model="formData.verificationCode" placeholder="输入验证码">
        <template #append>
          <el-button @click="sendCode">发送</el-button>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item>
      <div style="display: flex; justify-content: space-between; width: 100%;">
        <el-button @click="emit('prev')">上一步</el-button>
        <el-button type="primary" @click="complete">完成注册</el-button>
      </div>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import type { RegisterRequest } from '@/types/register'
import { computed, ref } from 'vue'
import { registerApi, sendRegistCaptchaApi } from '@/api/register'
import type { FormInstance, FormRules } from 'element-plus'
import { RSAUtil } from '@/utils/rsa'
import { getPublicKey } from '@/api/auth'

const props = defineProps<{ modelValue: RegisterRequest }>()
const emit = defineEmits<{
  (e: 'update:modelValue', val: RegisterRequest): void
  (e: 'prev'): void
  (e: 'done'): void
}>()

const formRef = ref<FormInstance>()

const rules: FormRules = {
  verificationCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const formData = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const sendCode = async () => {
  await sendRegistCaptchaApi(formData.value.email)
}

const complete = async () => {
  try {
    const valid = await formRef.value?.validate()
    if (valid) {
      const publicKey = await getPublicKey(0)
      formData.value.password = RSAUtil.encrypt(formData.value.password, publicKey)
      await registerApi(formData.value)
      emit('done')
    }
  } catch (error) {
    console.log(error)
  }
}
</script>
