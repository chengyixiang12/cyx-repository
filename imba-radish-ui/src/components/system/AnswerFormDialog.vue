<template>
<el-dialog title="重命名" v-model="visible" width="50vw" :close-on-click-modal="false"
    :before-close="handleClose">
    <div class="dialog-body-wrapper">
      <el-form ref="formRef" :model="formData" label-width="6vw" label-position="right"
        class="scrollable-form">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import type { FormInstance } from 'element-plus'
import { getTitleApi } from '@/api/dialogueHistory'
import { GetTitleVo } from '@/types/dialogueHistory';

const emit = defineEmits(['update:visible', 'submit']);
const formRef = ref<FormInstance>();
const formData = ref<GetTitleVo>({
    id: null,
    title: null
});

interface FatherParam {
  visible: boolean;
  dialogId: number | null;
}

const props = withDefaults(defineProps<FatherParam>(), {
  visible: false,
  dialogId: null,
})

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 获取标题
const getTitle = async () => {
    const res = await getTitleApi(props.dialogId);
    formData.value = res;
}

// 关闭弹窗事件
const handleClose = () => {
  emit('update:visible', false)
}

// 提交表单
const submitForm = async () => {
  const valid = await formRef.value?.validate()
  if (valid) {
    emit('submit', formData.value)
    emit('update:visible', false)
  }
}

onMounted(() => {
    getTitle();
})

</script>
<style scoped>
.dialog-body-wrapper {
  flex: 1;
  overflow-y: auto;
  max-height: 60vh;
  padding-right: 10px;
}

.dialog-body-wrapper::-webkit-scrollbar {
  height: 6px;
  width: 5px;
}
.dialog-body-wrapper::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.scrollable-form {
  padding: 25px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>