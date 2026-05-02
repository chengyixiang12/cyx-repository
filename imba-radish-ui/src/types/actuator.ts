export interface Metrics {
    name: string;
    description: string;
    baseUnit: string;
    measurements: {
        statistic: string;
        value: number;
    }[];
    availableTags: {}[];
}

export interface Health {
  status: string;
  components?: Record<string, HealthComponent>;
}

interface HealthComponent {
  status: string;
  details?: Record<string, any>;
}

export interface ListActuatorVO {
    createTime?: string;
    cpuUsage?: number;
    cpuCount?: number;
    memeryUsed?: number;
    memeryMax?: number;
    uptime?: number;
    diskFree?: number;
    diskTotal?: number;
    healthDb?: string;
    healthDiskSpace?: string;
    healthMail?: string;
    healthPing?: string;
    healthRabbit?: string;
    healthRedis?: string;
    healthSsl?: string;
    health?: string;
}