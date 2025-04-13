import { useMutation, useQueryClient } from '@tanstack/react-query'
import { animalApi } from '../api/animalApi'
import { AttributeRequest, AttributeResponse } from '../api/types'

export const useAnimalAttributes = (animalId: number) => {
	const queryClient = useQueryClient()

	const addAttribute = useMutation<AttributeResponse, Error, AttributeRequest>({
		mutationFn: data => animalApi.addAttribute(animalId, data),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animal', animalId] })
		},
	})

	const updateAttribute = useMutation<
		AttributeResponse,
		Error,
		{ attributeId: number; data: AttributeRequest }
	>({
		mutationFn: ({ attributeId, data }) =>
			animalApi.updateAttribute(animalId, attributeId, data),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animal', animalId] })
		},
	})

	const deleteAttribute = useMutation<void, Error, number>({
		mutationFn: attributeId => animalApi.deleteAttribute(animalId, attributeId),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animal', animalId] })
		},
	})

	return {
		addAttribute: addAttribute.mutateAsync,
		isAdding: addAttribute.isPending,
		updateAttribute: updateAttribute.mutateAsync,
		isUpdating: updateAttribute.isPending,
		deleteAttribute: deleteAttribute.mutateAsync,
		isDeleting: deleteAttribute.isPending,
	}
}
