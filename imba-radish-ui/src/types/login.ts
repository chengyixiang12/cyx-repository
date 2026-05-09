// 登录请求参数
export interface LoginRequest {
    username?: string;
    password?: string;
    loginMethod: string;
    graphicsCaptcha?: string; // 图形验证码
    uuid?: string;
    email?: string;
    emailCaptcha?: string;
    fingerprint?: string | null;
  }
  
  // 登录成功响应
  export interface LoginVo {
    token: string;
    // refreshToken?: string;
    // userInfo: {
    //   id: number;
    //   name: string;
    //   avatar?: string;
    //   // 其他用户信息...
    // };
  }

  export interface UserInfoVo {
    id: string;
    username: string | null;
    deptId?: number | null;
    nickname?: string | null;
    phone?: string | null;
    email?: string | null;
    avatar?: string | null;
    permissions?: Array<string>
  }
  
  // 后端通用响应结构
  export interface ApiResponse<T = any> {
    timestamp: number;
    code: number;
    msg: string;
    data: T;
  }