<template>
    <div class="log-detail-container">
        <!-- 返回按钮 -->
        <el-button type="primary" :icon="ArrowLeft" @click="handleBack" class="back-button">
            返回
        </el-button>
        <el-card class="detail-card">
            <div class="short-fields">
                <el-row :gutter="20">
                    <el-col v-for="item in displayData.filter(i => !i.isLong)" :key="item.label" :span="12" :xs="24" :sm="12" :md="8">
                        <div class="field-item">
                            <span class="field-label">{{ item.label }}</span>
                            <span class="field-value">{{ item.value }}</span>
                        </div>
                    </el-col>
                </el-row>
            </div>

            <div class="long-fields" v-for="item in displayData.filter(i => i.isLong)" :key="item.label">
                <div class="field-item">
                    <span class="field-label">{{ item.label }}：</span>
                    <div class="long-text">{{ item.value }}</div>
                </div>
            </div>
        </el-card>
    </div>
</template>


<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getLogDetailApi } from '@/api/log'

const route = useRoute()
const router = useRouter()
const logId = route.query.id as string
const displayData = ref<{ label: string, value: string, isLong?: boolean }[]>([])

const fields = [
    { label: '创建人', key: 'nickname' },
    { label: '创建时间', key: 'createTime' },
    { label: '日志级别', key: 'logLevel', formatter: (val: string) => formatLogLevel(val) },
    { label: '请求IP', key: 'ipAddress' },
    { label: '访问路径', key: 'requestUrl' },
    { label: '请求方法', key: 'requestMethod' },
    { label: '请求参数', key: 'requestParams', isLong: true },
    { label: '响应结果', key: 'responseResult', isLong: true },
    { label: '异常信息', key: 'exceptionInfo', isLong: true },
    { label: '操作描述', key: 'operationDesc' },
    { label: '耗时（ms）', key: 'executionTime' },
    { label: '模块名称', key: 'moduleName' },
    { label: '状态码', key: 'statusCode' },
    { label: '操作系统/浏览器', key: 'osBrowserInfo' },
]

const loadLogDetail = async () => {
    const res = await getLogDetailApi(logId)

    displayData.value = fields.map(({ label, key, formatter, isLong }) => ({
        label,
        value: formatter ? formatter((res as any)[key]) : ((res as any)[key] ?? '-'),
        isLong: isLong ?? false
    }))
}

const formatLogLevel = (level?: string) => {
    switch (level) {
        case '1': return '普通'
        case '2': return '警告'
        case '3': return '错误'
        default: return '-'
    }
}

const handleBack = () => {
    router.back()
}

onMounted(() => {
    loadLogDetail()
})
</script>

<style scoped>
.log-detail-container {
    height: calc(100vh - 170px);
    padding: 20px;
    background: #f8f9fa;
    overflow-y: auto;
    box-sizing: border-box;
}

.back-button {
    margin-bottom: 20px;
    --el-button-bg-color: #409eff;
    --el-button-border-color: #409eff;
}

.detail-card {
    padding: 24px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
    background: #ffffff;
}

.short-fields {
    margin-bottom: 24px;
}

.field-item {
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
}

.field-label {
    font-weight: 600;
    color: #909399;
    font-size: 14px;
}

.field-label::after {
    content: ':';
}

.field-value {
    color: #303133;
    font-size: 14px;
    line-height: 1.5;
}

.long-fields {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #e4e7ed;
}

.long-fields:first-child {
    margin-top: 0;
    padding-top: 0;
    border-top: none;
}

.long-text {
    margin-top: 8px;
    padding: 16px;
    background: #f5f7fa;
    border: 1px solid #e4e7ed;
    border-radius: 6px;
    color: #303133;
    white-space: pre-wrap;
    word-break: break-word;
    font-family: 'Courier New', Courier, monospace;
    font-size: 13px;
    line-height: 1.5;
}
</style>