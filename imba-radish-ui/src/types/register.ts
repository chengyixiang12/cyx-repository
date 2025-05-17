export interface RegisterRequest {
    username: string
    nickname: string | null
    email: string
    password: string
    confirmPassword: string
    verificationCode: string
}