import { apiClient } from '../../../api/client'
import {
	AnimalCreateRequest,
	AnimalResponse,
	AnimalUpdateRequest,
	AttributeRequest,
	AttributeResponse,
	S3FileResponse,
	StatusLogResponse,
} from './types'

export const animalApi = {
	// Основные операции
	createAnimal: (data: AnimalCreateRequest) =>
		apiClient.post<AnimalResponse>('/animals', data).then(r => r.data),

	getAnimal: (animalId: number) =>
		apiClient.get<AnimalResponse>(`/animals/${animalId}`).then(r => r.data),

	updateAnimal: (animalId: number, data: AnimalUpdateRequest) =>
		apiClient
			.patch<AnimalResponse>(`/animals/${animalId}`, data)
			.then(r => r.data),

	deleteAnimal: (animalId: number) =>
		apiClient.delete(`/animals/${animalId}`).then(r => r.data),

	// Атрибуты
	addAttribute: (animalId: number, data: AttributeRequest) =>
		apiClient
			.post<AttributeResponse>(`/animals/${animalId}/attributes`, data)
			.then(r => r.data),

	updateAttribute: (
		animalId: number,
		attributeId: number,
		data: AttributeRequest
	) =>
		apiClient
			.patch<AttributeResponse>(
				`/animals/${animalId}/attributes/${attributeId}`,
				data
			)
			.then(r => r.data),

	deleteAttribute: (animalId: number, attributeId: number) =>
		apiClient
			.delete(`/animals/${animalId}/attributes/${attributeId}`)
			.then(r => r.data),

	// Фото
	uploadAnimalPhoto: (animalId: number, file: File) => {
		const formData = new FormData()
		formData.append('file', file)
		return apiClient
			.post<S3FileResponse>(`/animals/${animalId}/photos`, formData, {
				headers: { 'Content-Type': 'multipart/form-data' },
			})
			.then(r => r.data)
	},

	deleteAnimalPhoto: (photoId: number) =>
		apiClient.delete(`/animals/photos/${photoId}`).then(r => r.data),

	// Документы
	uploadAnimalDocument: (animalId: number, file: File, type: string) => {
		const formData = new FormData()
		formData.append('file', file)
		formData.append('type', type)
		return apiClient
			.post<S3FileResponse>(`/animals/${animalId}/documents`, formData, {
				headers: { 'Content-Type': 'multipart/form-data' },
			})
			.then(r => r.data)
	},

	deleteAnimalDocument: (documentId: number) =>
		apiClient.delete(`/animals/documents/${documentId}`).then(r => r.data),

	// История состояний
	getStatusLogs: (animalId: number) =>
		apiClient
			.get<StatusLogResponse[]>(`/animals/${animalId}/status-logs`)
			.then(r => r.data),
	
}
