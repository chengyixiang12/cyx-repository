import { get } from '@/utils/http';

export async function getMessageNumApi(): Promise<number> {
    const res = await get('/message/getMessage', { flag: true })
    return res.data;
}