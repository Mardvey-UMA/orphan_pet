import { useMutation } from '@tanstack/react-query'
import { authApi } from '../api/authApi'
import { useToken } from './useToken'

export const useAuth = () => {
	const { setTokens, clearTokens } = useToken()

	const registerMutation = useMutation({
		mutationFn: authApi.register,
	})

	const activateMutation = useMutation({
		mutationFn: authApi.activate,
	})

	const loginMutation = useMutation({
		mutationFn: authApi.login,
		onSuccess: data => {
			setTokens({
				accessToken: data.access_token,
				refreshToken: data.refresh_token,
			})
		},
	})

	const logout = () => {
		clearTokens()
	}

	return {
		register: registerMutation.mutateAsync,
		isRegistering: registerMutation.isPending,

		activate: activateMutation.mutateAsync,
		isActivating: activateMutation.isPending,

		login: loginMutation.mutateAsync,
		isLoggingIn: loginMutation.isPending,

		logout,
	}
}
