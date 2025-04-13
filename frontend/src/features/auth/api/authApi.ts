import { apiClient } from '../../../api/client'
import {
	LoginRequest,
	LoginResponse,
	RegisterRequest,
	RegisterResponse,
} from './types'

export const authApi = {
	register: (data: RegisterRequest) =>
		apiClient
			.post<RegisterResponse>('/auth/register', data)
			.then(response => response.data),

	activate: (token: string) =>
		apiClient
			.get<void>('/auth/activate-account', {
				params: { token },
			})
			.then(r => r.data),

	login: (data: LoginRequest) =>
		apiClient
			.post<LoginResponse>('/auth/authenticate', data)
			.then(response => response.data),
}
