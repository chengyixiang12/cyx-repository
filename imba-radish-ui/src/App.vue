<script setup lang="ts">
</script>

<template>
  <div>
    <router-view></router-view>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}
.custom-message {
  top: 20% !important;  /* 控制垂直位置 */
  left: 50% !important;
  transform: translateX(-50%) !important;
  min-width: 200px !important;
  text-align: center !important;
}
</style>
