<template>
    <div class="container">
        <!-- 头部: 返回按钮统一在左上角 -->
        <div class="list-header">
            <div class="header-title">
                <el-button link @click="handleBack" class="go-back">
                    <el-icon><ArrowLeft /></el-icon>返回
                </el-button>
                <el-divider direction="vertical" />
                <span>日志详情</span>
            </div>
        </div>

        <div class="detail-wrapper">
            <div class="detail-card">
                <!-- 短字段 -->
                <el-row :gutter="20">
                    <el-col v-for="item in displayData.filter(i => !i.isLong)" :key="item.label" :span="12" :xs="24"
                        :sm="12" :md="8">
                        <div class="field-item">
                            <span class="field-label">{{ item.label }}</span>
                            <el-tag v-if="item.label === '日志级别' && item.value !== '-'" :type="logLevelTagType(item.value)">
                                {{ item.value }}
                            </el-tag>
                            <span v-else class="field-value">{{ item.value }}</span>
                        </div>
                    </el-col>
                </el-row>

                <!-- 长文本字段 -->
                <div class="long-fields" v-for="item in displayData.filter(i => i.isLong)" :key="item.label">
                    <div class="field-item is-block">
                        <span class="field-label">{{ item.label }}：</span>
                        <div class="long-text">{{ item.value }}</div>
                    </div>
                </div>
            </div>
        </div>
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

// 日志级别对应的标签颜色
const logLevelTagType = (level: string) => {
    switch (level) {
        case '警告': return 'warning'
        case '错误': return 'danger'
        default: return 'info'
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
/* 返回按钮: 统一左上角, link 风格 */
.go-back {
    font-size: var(--radish-font-base);
    color: var(--radish-text-regular);
}

.go-back .el-icon {
    margin-right: 2px;
}

.go-back:hover {
    color: var(--radish-primary);
}

/* 内容区: 滚动 + 白色卡片 */
.detail-wrapper {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
    padding: 12px;
}

.detail-card {
    background: var(--radish-bg-card);
    border: 1px solid var(--radish-border);
    border-radius: var(--radish-radius-lg);
    box-shadow: var(--radish-shadow-card);
    padding: 20px 24px;
}

.short-fields {
    margin-bottom: 8px;
}

.field-item {
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
}

.field-item.is-block {
    display: block;
}

.field-label {
    font-weight: 600;
    color: var(--radish-text-secondary);
    font-size: var(--radish-font-base);
}

.field-label::after {
    content: ':';
}

.field-value {
    color: var(--radish-text-primary);
    font-size: var(--radish-font-base);
    line-height: 1.5;
    word-break: break-all;
}

.long-fields {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid var(--radish-border-light);
}

.long-fields:first-of-type {
    margin-top: 0;
    padding-top: 0;
    border-top: none;
}

.long-text {
    margin-top: 8px;
    padding: 14px 16px;
    background: var(--radish-fill);
    border: 1px solid var(--radish-border);
    border-radius: var(--radish-radius);
    color: var(--radish-text-primary);
    white-space: pre-wrap;
    word-break: break-word;
    font-family: 'Courier New', Courier, monospace;
    font-size: var(--radish-font-sm);
    line-height: 1.6;
    max-height: 420px;
    overflow-y: auto;
}
</style>
