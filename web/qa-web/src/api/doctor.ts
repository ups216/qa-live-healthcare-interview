import request from './request';

/**
 * 医生对外数据模型。
 *
 * 字段与后端 dto/DoctorVO 保持一致；
 * 注意这里**没有 password** 字段 —— 后端已在 VO 层剔除，
 * 医生登录（DoctorLogin.vue）改走 store 内部的本地凭证比对（见方案决策 D5）。
 */
export interface DoctorVO {
  id: string;
  username: string;
  name: string;
  title: string;
  department: string;
  avatar: string;
  experience: string;
  specialties: string[];
  isActive: boolean;
}

/** 全部医生列表 */
export function fetchDoctors(): Promise<DoctorVO[]> {
  return request.get<DoctorVO[]>('/doctors').then((res) => res.data);
}

/** 仅在线医生 */
export function fetchActiveDoctors(): Promise<DoctorVO[]> {
  return request.get<DoctorVO[]>('/doctors/active').then((res) => res.data);
}

/** 按主键查询，不存在时为 null */
export async function fetchDoctorById(id: string): Promise<DoctorVO | null> {
  try {
    const res = await request.get<DoctorVO>(`/doctors/${encodeURIComponent(id)}`);
    return res.data;
  } catch {
    // 后端对不存在返回 404，此处按资源未找到处理，不向上抛业务错误
    return null;
  }
}

/**
 * 按登录名查询，不存在时为 null。
 * Consultation.vue 依据路由参数 /consultation/:doctorUsername 调用。
 */
export async function fetchDoctorByUsername(username: string): Promise<DoctorVO | null> {
  try {
    const res = await request.get<DoctorVO>(`/doctors/username/${encodeURIComponent(username)}`);
    return res.data;
  } catch {
    return null;
  }
}
