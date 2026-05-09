<template>
  <div>
    <el-config-provider :size="componentSize" :locale="locale">
      <router-view></router-view>
    </el-config-provider>

  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

const componentSize = ref<'small'>('small')
const locale = ref(zhCn)
</script>

<style scoped>
* {
  box-sizing: border-box;
}
</style>
