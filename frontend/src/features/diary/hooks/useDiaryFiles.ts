import { useMutation, useQueryClient } from '@tanstack/react-query'
import { diaryApi } from '../api/diaryApi'

export const useDiaryFiles = (animalId: number, statusLogId: number) => {
	const queryClient = useQueryClient()

	const addPhoto = useMutation({
		mutationFn: (file: File) =>
			diaryApi.addStatusLogPhoto(animalId, statusLogId, file),
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ['animalStatusLogs', animalId],
			})
		},
	})

	const deletePhoto = useMutation({
		mutationFn: (photoId: number) => diaryApi.deleteStatusLogPhoto(photoId),
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ['animalStatusLogs', animalId],
			})
		},
	})

	const addDocument = useMutation({
		mutationFn: ({ file, type }: { file: File; type: string }) =>
			diaryApi.addStatusLogDocument(animalId, statusLogId, file, type),
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ['animalStatusLogs', animalId],
			})
		},
	})

	const deleteDocument = useMutation({
		mutationFn: (documentId: number) =>
			diaryApi.deleteStatusLogDocument(documentId),
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ['animalStatusLogs', animalId],
			})
		},
	})

	return {
		addPhoto: addPhoto.mutateAsync,
		isAddingPhoto: addPhoto.isPending,

		deletePhoto: deletePhoto.mutateAsync,
		isDeletingPhoto: deletePhoto.isPending,

		addDocument: addDocument.mutateAsync,
		isAddingDocument: addDocument.isPending,

		deleteDocument: deleteDocument.mutateAsync,
		isDeletingDocument: deleteDocument.isPending,
	}
}
