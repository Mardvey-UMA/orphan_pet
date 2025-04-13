import { useMutation, useQuery } from '@tanstack/react-query'
import { UserUpdateRequest } from '../api/types'
import { userApi } from '../api/userApi'

export const useUser = () => {
	const {
		data: currentUser,
		isPending,
		refetch,
	} = useQuery({
		queryKey: ['currentUser'],
		queryFn: userApi.getCurrentUser,
	})

	const updateUser = useMutation({
		mutationFn: (data: UserUpdateRequest) => userApi.updateUser(data),
		onSuccess: () => refetch(),
	})

	const uploadPhoto = useMutation({
		mutationFn: (file: File) => userApi.uploadPhoto(file),
		onSuccess: () => refetch(),
	})

	return {
		currentUser,
		isPending,
		updateUser: updateUser.mutateAsync,
		isUpdating: updateUser.isPending,
		uploadPhoto: uploadPhoto.mutateAsync,
		isUploading: uploadPhoto.isPending,
	}
}
