import { useEffect } from 'react'
import { apiClient } from '../../../api/client'

export const useToken = () => {
	const setTokens = (tokens: { accessToken: string; refreshToken: string }) => {
		localStorage.setItem('accessToken', tokens.accessToken)
		localStorage.setItem('refreshToken', tokens.refreshToken)
		apiClient.defaults.headers['Authorization'] = `Bearer ${tokens.accessToken}`
	}

	const clearTokens = () => {
		localStorage.removeItem('accessToken')
		localStorage.removeItem('refreshToken')
		delete apiClient.defaults.headers['Authorization']
	}

	const getAccessToken = () => localStorage.getItem('accessToken')

	// Инициализация при монтировании
	useEffect(() => {
		const token = localStorage.getItem('accessToken')
		if (token) {
			apiClient.defaults.headers['Authorization'] = `Bearer ${token}`
		}
	}, [])

	return { setTokens, clearTokens, getAccessToken }
}
