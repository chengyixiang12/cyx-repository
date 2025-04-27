<template>
    <div class="log-detail-container">
        <!-- 返回按钮 -->
        <el-button type="primary" icon="ArrowLeft" @click="handleBack" class="back-button">
            返回
        </el-button>
        <el-card>
            <div class="short-fields">
                <el-row :gutter="20">
                    <el-col v-for="item in displayData.filter(i => !i.isLong)" :key="item.label" :span="8">
                        <div class="field-item">
                            <span class="field-label">{{ item.label }}：</span>
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
import { getLogDetailApi } from '@/api/log'

const route = useRoute()
const router = useRouter()
const logId = route.query.id as unknown as number
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
    padding: 20px;
    background: #f5f7fa;
}

.back-button {
    margin-bottom: 16px;
}

.detail-card {
    padding: 20px;
}

.field-item {
    margin-bottom: 16px;
}

.field-label {
    font-weight: bold;
    color: #333;
}

.field-value {
    margin-left: 8px;
    color: #666;
}

.long-text {
    margin-top: 8px;
    padding: 10px;
    background: #fafafa;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    color: #555;
    white-space: pre-wrap;
    word-break: break-word;
}
</style>