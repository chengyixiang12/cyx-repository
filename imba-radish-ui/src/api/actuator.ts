import { Health, Metrics } from '@/types/actuator';
import { getActuator } from '@/utils/http';

/**
 * 获取cpu使用率
 */
export async function getCpuUsageApi(): Promise<Metrics> {
    const res = await getActuator<Metrics>('/metrics/system.cpu.usage');
    return res;
}


/**
 * 获取cpu核数
 * @returns 
 */
export async function getCpuCoreCountApi(): Promise<Metrics> {
    const res = await getActuator<Metrics>('/metrics/system.cpu.count');
    return res;
}

/**
 * 获取jvm内存使用量
 */
export async function getMemoryUsageApi(): Promise<Metrics> {
    const res = await getActuator<Metrics>('/metrics/jvm.memory.used');
    return res;
}

/**
 * 获取jvm内存总量
 */
export async function getMemoryTotalApi(): Promise<Metrics> {
    const res = await getActuator<Metrics>('/metrics/jvm.memory.max');
    return res;
}

/**
 * 获取服务状态
 */
export async function getHealthApi(): Promise<Health> {
    const res = await getActuator<Health>('/health');
    return res;
}

/**
 * 获取系统启动时间
 */
export async function getUptimeApi(): Promise<Metrics> {
    const res = await getActuator<Metrics>('/metrics/process.uptime');
    return res;
}

/**
 * 获取磁盘使用量
 */
export async function getDiskFreeApi(): Promise<Metrics> {
    const res = await getActuator<Metrics>('/metrics/disk.free');
    return res;
}

/** 
 * 获取磁盘总量
*/
export async function getDiskTotalApi(): Promise<Metrics> {
    const res = await getActuator<Metrics>('/metrics/disk.total');
    return res;
}