// Регистрация
export interface RegisterRequest {
	username: string
	email: string
	password: string
}

export interface RegisterResponse {
	email: string
	username: string
	isActivated: boolean
}

// Активация
export interface ActivateRequest {
	token: string
}

// Авторизация
export interface LoginRequest {
	identifier: string // email или username
	password: string
}

export interface Tokens {
	accessToken: string
	refreshToken: string
	accessExpiresAt: string
	refreshExpiresAt: string
}

export interface LoginResponse extends Tokens {
	user: {
		email: string
		username: string
	}
}
