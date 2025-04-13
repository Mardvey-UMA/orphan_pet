import { useMutation, useQuery } from '@tanstack/react-query'
import { userApi } from '../api/userApi'

export const useUser = () => {
	// Получение текущего пользователя
	const {
		data: currentUser,
		isLoading: isUserLoading,
		refetch: refetchUser,
	} = useQuery({
		queryKey: ['currentUser'],
		queryFn: userApi.getCurrentUser,
		staleTime: 5 * 60 * 1000, // 5 минут
	})

	// Обновление данных
	const updateMutation = useMutation({
		mutationFn: userApi.updateUser,
		onSuccess: () => refetchUser(),
	})

	// Загрузка фото
	const uploadPhotoMutation = useMutation({
		mutationFn: userApi.uploadPhoto,
		onSuccess: () => refetchUser(),
	})

	return {
		currentUser,
		isUserLoading,

		updateUser: updateMutation.mutateAsync,
		isUpdating: updateMutation.isPending,

		uploadPhoto: uploadPhotoMutation.mutateAsync,
		isUploading: uploadPhotoMutation.isPending,
	}
}
