
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { animalApi } from '../api/animalApi'
import { AnimalUpdateRequest } from '../api/types'
import { useToken } from '../../auth/hooks/useToken'

export const useAnimal = (animalId?: number) => {
	const queryClient = useQueryClient()
	const { getAccessToken } = useToken()

	// Получение данных животного
	const animalQuery = useQuery({
		queryKey: ['animal', animalId],
		queryFn: () => animalApi.getAnimal(animalId!),
		enabled: !!animalId && !!getAccessToken(),
		staleTime: 5 * 60 * 1000,
	})

	// Создание животного
	const createMutation = useMutation({
		mutationFn: animalApi.createAnimal,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['userAnimals'] })
		},
	})

	// Обновление животного
	const updateMutation = useMutation({
		mutationFn: ({ id, data }: { id: number; data: AnimalUpdateRequest }) =>
			animalApi.updateAnimal(id, data),
		onSuccess: (_, { id }) => {
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
		},
	})

	// Удаление животного
	const deleteMutation = useMutation({
		mutationFn: animalApi.deleteAnimal,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['userAnimals'] })
		},
	})

	return {
		// Данные
		animalData: animalQuery.data,
		isLoading: animalQuery.isLoading,
		refetchAnimal: animalQuery.refetch,

		// Основные операции
		createAnimal: createMutation.mutateAsync,
		isCreating: createMutation.isPending,

		updateAnimal: updateMutation.mutateAsync,
		isUpdating: updateMutation.isPending,

		deleteAnimal: deleteMutation.mutateAsync,
		isDeleting: deleteMutation.isPending,
	}
}
