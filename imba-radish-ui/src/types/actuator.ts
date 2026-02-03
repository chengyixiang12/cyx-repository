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