import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { animalApi } from '../api/animalApi'
import { AnimalCreateRequest, AnimalResponse } from '../api/types'

export const useAnimal = (animalId?: number) => {
	const queryClient = useQueryClient()

	const {
		data: animal,
		isPending,
		error,
	} = useQuery<AnimalResponse>({
		queryKey: ['animal', animalId],
		queryFn: () => animalApi.getAnimal(animalId!),
		enabled: !!animalId,
	})

	const createMutation = useMutation({
		mutationFn: animalApi.createAnimal,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['userAnimals'] })
		},
	})

	const updateMutation = useMutation({
		mutationFn: ({
			id,
			data,
		}: {
			id: number
			data: Partial<AnimalCreateRequest>
		}) => animalApi.updateAnimal(id, data),
		onSuccess: (_, { id }) => {
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
			queryClient.invalidateQueries({ queryKey: ['userAnimals'] })
		},
	})

	const deleteMutation = useMutation({
		mutationFn: (id: number) => animalApi.deleteAnimal(id),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['userAnimals'] })
		},
	})

	return {
		animal,
		isPending,
		error,
		createAnimal: createMutation.mutateAsync,
		isCreating: createMutation.isPending,
		updateAnimal: updateMutation.mutateAsync,
		isUpdating: updateMutation.isPending,
		deleteAnimal: deleteMutation.mutateAsync,
		isDeleting: deleteMutation.isPending,
	}
}
