import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { AttributeRequest } from '../../animal/api/types'
import { useToken } from '../../auth/hooks/useToken'
import { diaryApi } from '../api/diaryApi'

export const useDiaryAttributes = (animalId: number, statusLogId?: number) => {
	const queryClient = useQueryClient()
	const { getAccessToken } = useToken()

	// История атрибутов животного
	const attributeHistoryQuery = useQuery({
		queryKey: ['animalAttributeHistory', animalId],
		queryFn: () => diaryApi.getAttributeHistory(animalId),
		enabled: !!animalId && !!getAccessToken(),
	})

	// Операции с атрибутами записи
	const addAttribute = useMutation({
		mutationFn: (data: AttributeRequest) =>
			diaryApi.addStatusLogAttribute(animalId, statusLogId!, data),
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ['animalStatusLogs', animalId],
			})
		},
	})

	const updateAttribute = useMutation({
		mutationFn: ({
			attributeId,
			data,
		}: {
			attributeId: number
			data: AttributeRequest
		}) =>
			diaryApi.updateStatusLogAttribute(
				animalId,
				statusLogId!,
				attributeId,
				data
			),
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ['animalStatusLogs', animalId],
			})
			queryClient.invalidateQueries({
				queryKey: ['animalAttributeHistory', animalId],
			})
		},
	})

	const deleteAttribute = useMutation({
		mutationFn: (attributeId: number) =>
			diaryApi.deleteStatusLogAttribute(animalId, statusLogId!, attributeId),
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ['animalStatusLogs', animalId],
			})
			queryClient.invalidateQueries({
				queryKey: ['animalAttributeHistory', animalId],
			})
		},
	})

	return {
		attributeHistory: attributeHistoryQuery.data,
		isLoadingHistory: attributeHistoryQuery.isLoading,

		addAttribute: addAttribute.mutateAsync,
		isAddingAttribute: addAttribute.isPending,

		updateAttribute: updateAttribute.mutateAsync,
		isUpdatingAttribute: updateAttribute.isPending,

		deleteAttribute: deleteAttribute.mutateAsync,
		isDeletingAttribute: deleteAttribute.isPending,
	}
}
