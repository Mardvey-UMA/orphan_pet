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
		{ attrId: number; data: AttributeRequest }
	>({
		mutationFn: ({ attrId, data }) =>
			animalApi.updateAttribute(animalId, attrId, data),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animal', animalId] })
		},
	})

	const deleteAttribute = useMutation<void, Error, number>({
		mutationFn: attrId => animalApi.deleteAttribute(animalId, attrId),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animal', animalId] })
		},
	})

	return {
		addAttribute: addAttribute.mutateAsync,
		isAdding: addAttribute.isPending,

		updateAttribute: updateAttribute.mutateAsync,
		isUpdatingAttr: updateAttribute.isPending,

		deleteAttribute: deleteAttribute.mutateAsync,
		isDeletingAttr: deleteAttribute.isPending,
	}
}
