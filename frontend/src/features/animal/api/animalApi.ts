import { apiClient } from '../../../api/client'
import {
	AnimalCreateRequest,
	AnimalDocumentUploadResponse,
	AnimalPhotoUploadResponse,
	AnimalResponse,
	AttributeRequest,
} from './types'

export const animalApi = {
	// Основные операции с животными
	createAnimal: (data: AnimalCreateRequest) =>
		apiClient.post<AnimalResponse>('/animals', data).then(res => res.data),

	getAnimal: (id: number) =>
		apiClient.get<AnimalResponse>(`/animals/${id}`).then(res => res.data),

	updateAnimal: (id: number, data: Partial<AnimalCreateRequest>) =>
		apiClient
			.patch<AnimalResponse>(`/animals/${id}`, data)
			.then(res => res.data),

	deleteAnimal: (id: number) =>
		apiClient.delete(`/animals/${id}`).then(res => res.data),

	// Файлы
	uploadAnimalPhoto: (animalId: number, file: File) => {
		const formData = new FormData()
		formData.append('file', file)
		return apiClient
			.post<AnimalPhotoUploadResponse>(
				`/animals/${animalId}/media/photos`,
				formData,
				{ headers: { 'Content-Type': 'multipart/form-data' } }
			)
			.then(res => res.data)
	},

	uploadAnimalDocument: (animalId: number, file: File, type: string) => {
		const formData = new FormData()
		formData.append('file', file)
		formData.append('type', type)
		return apiClient
			.post<AnimalDocumentUploadResponse>(
				`/animals/${animalId}/media/documents`,
				formData,
				{ headers: { 'Content-Type': 'multipart/form-data' } }
			)
			.then(res => res.data)
	},
	deleteAnimalPhoto: (photoUrl: string) => {
		return apiClient
			.delete(`/animals/photos`, {
				data: { url: photoUrl },
			})
			.then(res => res.data)
	},

	deleteAnimalDocument: (documentUrl: string) => {
		return apiClient
			.delete(`/animals/documents`, {
				data: { url: documentUrl },
			})
			.then(res => res.data)
	},
	// Атрибуты
	addAttribute: (animalId: number, data: AttributeRequest) =>
		apiClient
			.post(`/animals/${animalId}/attributes`, data)
			.then(res => res.data),

	updateAttribute: (
		animalId: number,
		attributeId: number,
		data: AttributeRequest
	) =>
		apiClient
			.patch(`/animals/${animalId}/attributes/${attributeId}`, data)
			.then(res => res.data),

	deleteAttribute: (animalId: number, attributeId: number) =>
		apiClient
			.delete(`/animals/${animalId}/attributes/${attributeId}`)
			.then(res => res.data),
	getUserAnimals: () =>
		apiClient.get<AnimalResponse[]>('/users/me/animals').then(res => res.data),
}
