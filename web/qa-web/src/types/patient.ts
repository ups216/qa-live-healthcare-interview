/** 患者（API 返回，不含密码） */
export interface Patient {
  id: string;
  username: string;
  name: string;
  birthday?: string;
  phone?: string;
  gender?: string;
}
