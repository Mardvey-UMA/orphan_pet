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
	indentifier: string // email или username
	password: string
}

export interface Tokens {
	access_token: string
	refresh_token: string
	access_expires_at: string
	refresh_expires_at: string
}

export interface LoginResponse extends Tokens {
	user: {
		email: string
		username: string
	}
}
