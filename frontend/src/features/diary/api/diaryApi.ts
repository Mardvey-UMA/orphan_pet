import { apiClient } from '../../../api/client'
import { AttributeRequest } from '../../animal/api/types'
import {
	AttributeHistoryResponse,
	StatusLogCreateRequest,
	StatusLogResponse,
	StatusLogUpdateRequest,
	StatusLogWithHistory,
} from './types'

export const diaryApi = {
	// Основные операции с записями
	createStatusLog: (animalId: number, data: StatusLogCreateRequest) =>
		apiClient
			.post<StatusLogResponse>(`/animals/${animalId}/status-logs`, data)
			.then(r => r.data),

	getStatusLog: (animalId: number, statusLogId: number) =>
		apiClient
			.get<StatusLogWithHistory>(
				`/animals/${animalId}/status-logs/${statusLogId}`
			)
			.then(r => r.data),
	getStatusLogs: (animalId: number) =>
		apiClient
			.get<StatusLogResponse[]>(`/animals/${animalId}/status-logs`)
			.then(r => r.data),
	updateStatusLog: (
		animalId: number,
		statusLogId: number,
		data: StatusLogUpdateRequest
	) =>
		apiClient
			.put<StatusLogResponse>(
				`/animals/${animalId}/status-logs/${statusLogId}`,
				data
			)
			.then(r => r.data),

	deleteStatusLog: (animalId: number, statusLogId: number) =>
		apiClient
			.delete(`/animals/${animalId}/status-logs/${statusLogId}`)
			.then(r => r.data),

	// Файлы записей
	addStatusLogPhoto: (animalId: number, statusLogId: number, file: File) => {
		const formData = new FormData()
		formData.append('file', file)
		return apiClient
			.post(
				`/animals/${animalId}/status-logs/${statusLogId}/photos`,
				formData,
				{ headers: { 'Content-Type': 'multipart/form-data' } }
			)
			.then(r => r.data)
	},

	deleteStatusLogPhoto: (photoId: number) =>
		apiClient
			.delete(`/animals/status-logs/photos/${photoId}`)
			.then(r => r.data),

	addStatusLogDocument: (
		animalId: number,
		statusLogId: number,
		file: File,
		type: string
	) => {
		const formData = new FormData()
		formData.append('file', file)
		formData.append('type', type)
		return apiClient
			.post(
				`/animals/${animalId}/status-logs/${statusLogId}/documents`,
				formData,
				{ headers: { 'Content-Type': 'multipart/form-data' } }
			)
			.then(r => r.data)
	},

	deleteStatusLogDocument: (documentId: number) =>
		apiClient
			.delete(`/animals/status-logs/documents/${documentId}`)
			.then(r => r.data),

	// Атрибуты записей
	addStatusLogAttribute: (
		animalId: number,
		statusLogId: number,
		data: AttributeRequest
	) =>
		apiClient
			.post(`/animals/${animalId}/status-logs/${statusLogId}/attributes`, data)
			.then(r => r.data),

	updateStatusLogAttribute: (
		animalId: number,
		statusLogId: number,
		attributeId: number,
		data: AttributeRequest
	) =>
		apiClient
			.patch(
				`/animals/${animalId}/status-logs/${statusLogId}/attributes/${attributeId}`,
				data
			)
			.then(r => r.data),

	deleteStatusLogAttribute: (
		animalId: number,
		statusLogId: number,
		attributeId: number
	) =>
		apiClient
			.delete(
				`/animals/${animalId}/status-logs/${statusLogId}/attributes/${attributeId}`
			)
			.then(r => r.data),

	// Получение истории атрибутов
	getAttributeHistory: (animalId: number) =>
		apiClient
			.get<AttributeHistoryResponse[]>(
				`/animals/${animalId}/attributes/history`
			)
			.then(r => r.data),
}
