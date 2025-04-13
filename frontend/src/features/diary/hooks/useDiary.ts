import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useToken } from '../../auth/hooks/useToken'
import { diaryApi } from '../api/diaryApi'
import { StatusLogCreateRequest, StatusLogUpdateRequest } from '../api/types'

export const useDiary = (animalId: number) => {
	const queryClient = useQueryClient()
	const { getAccessToken } = useToken()

	// Получение всех записей
	const statusLogsQuery = useQuery({
		queryKey: ['animalStatusLogs', animalId],
		queryFn: () => diaryApi.getStatusLogs(animalId),
		enabled: !!animalId && !!getAccessToken(),
	})

	// Создание записи
	const createMutation = useMutation({
		mutationFn: (data: StatusLogCreateRequest) =>
			diaryApi.createStatusLog(animalId, data),
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ['animalStatusLogs', animalId],
			})
		},
	})

	// Обновление записи
	const updateMutation = useMutation({
		mutationFn: ({
			statusLogId,
			data,
		}: {
			statusLogId: number
			data: StatusLogUpdateRequest
		}) => diaryApi.updateStatusLog(animalId, statusLogId, data),
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ['animalStatusLogs', animalId],
			})
		},
	})

	// Удаление записи
	const deleteMutation = useMutation({
		mutationFn: (statusLogId: number) =>
			diaryApi.deleteStatusLog(animalId, statusLogId),
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ['animalStatusLogs', animalId],
			})
		},
	})

	return {
		statusLogs: statusLogsQuery.data,
		isLoading: statusLogsQuery.isLoading,
		refetchStatusLogs: statusLogsQuery.refetch,

		createStatusLog: createMutation.mutateAsync,
		isCreating: createMutation.isPending,

		updateStatusLog: updateMutation.mutateAsync,
		isUpdating: updateMutation.isPending,

		deleteStatusLog: deleteMutation.mutateAsync,
		isDeleting: deleteMutation.isPending,
	}
}
