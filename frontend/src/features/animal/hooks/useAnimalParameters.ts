import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { animalApi } from '../api/animalApi'
import { AnimalUpdateRequest } from '../api/types'

export const useAnimalParameters = (animalId: number) => {
	const queryClient = useQueryClient()

	const { data } = useQuery({
		queryKey: ['animal', animalId],
		queryFn: () => animalApi.getAnimal(animalId),
	})

	const updateMutation = useMutation({
		mutationFn: (data: AnimalUpdateRequest) =>
			animalApi.updateAnimal(animalId, data),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animal', animalId] })
		},
	})

	return {
		parameters: data,
		updateParameters: updateMutation.mutateAsync,
		isUpdating: updateMutation.isPending,
	}
}
