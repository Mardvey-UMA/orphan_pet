import { apiClient } from '../../../api/client'
import { S3FileResponse, UserResponse, UserUpdateRequest } from './types'

export const userApi = {
	getCurrentUser: () =>
		apiClient.get<UserResponse>('/users/me').then(r => r.data),

	updateUser: (data: UserUpdateRequest) =>
		apiClient.patch<UserResponse>('/users/me', data).then(r => r.data),

	uploadPhoto: (file: File) => {
		const formData = new FormData()
		formData.append('file', file)
		return apiClient
			.post<S3FileResponse>('/users/me/photo', formData, {
				headers: {
					'Content-Type': 'multipart/form-data',
				},
			})
			.then(r => r.data)
	},
}
