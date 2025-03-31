<template>
    <div class="user-management-container">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>用户管理</span>
            <el-button type="primary" @click="handleAdd">新增用户</el-button>
          </div>
        </template>
        
        <!-- 用户表格 -->
        <el-table :data="userList" border>
            <el-table-column prop="id" label="主键" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="phone" label="手机号码" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="enabled" label="用户是否被启用" />
          <el-table-column prop="deptId" label="部门id" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <el-pagination
          class="pagination"
          :current-page="pagination.current"
          :page-size="pagination.size"
          :total="pagination.total"
          @current-change="handlePageChange"
        />
      </el-card>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { ref, onMounted } from 'vue'
  import { getUserList } from '@/api/user'
  import { User } from '@/types/user'
  
  const userList = ref<User[]>([])
  const pagination = ref({
    current: 1,
    size: 10,
    total: 0
  })
  
  // 加载用户数据
  const loadUsers = async () => {
    try {
      const res = await getUserList({
        page: pagination.value.current,
        size: pagination.value.size
      })
      userList.value = res.result;
      pagination.value.total = res.total;
    } catch (error) {
      console.error('加载用户列表失败:', error)
    }
  }
  
  // 页面操作
  const handleAdd = () => { /* 新增逻辑 */ }
  const handleEdit = (row: User) => { /* 编辑逻辑 */ }
  const handleDelete = (row: User) => { /* 删除逻辑 */ }
  const handlePageChange = (page: number) => {
    pagination.value.current = page
    loadUsers()
  }
  
  // 初始化加载
  onMounted(() => {
    loadUsers()
  })
  </script>
  
  <style scoped>
  .user-management-container {
    padding: 20px;
  }
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
  </style>