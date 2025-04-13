import axios from 'axios'

export const apiClient = axios.create({
	baseURL: import.meta.env.VITE_API_URL || '/api',
	withCredentials: true,
})

apiClient.interceptors.response.use(
	response => response,
	error => {
		if (error.response?.status === 401) {
			console.log('Ошибка атворизации')
		}
		return Promise.reject(error)
	}
)

// Инициализация токена при старте
const token = localStorage.getItem('accessToken')
if (token) {
	apiClient.defaults.headers['Authorization'] = `Bearer ${token}`
}
