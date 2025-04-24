import { AttributeResponse } from '@/features/animal/api/types'
import { apiClient } from '../../../api/client'
import {
	AttributeHistoryResponse,
	StatusLogCreateRequest,
	StatusLogResponse,
	StatusLogUpdateRequest,
} from './types'

export const diaryApi = {
	getStatusLogs: (animalId: number) =>
		apiClient.get<StatusLogResponse[]>(`/animals/${animalId}/status-logs`),

	createStatusLog: (animalId: number, data: StatusLogCreateRequest) =>
		apiClient.post<StatusLogResponse>(`/animals/${animalId}/status-logs`, data),

	updateStatusLog: (
		animalId: number,
		statusLogId: number,
		data: StatusLogUpdateRequest
	) =>
		apiClient.put<StatusLogResponse>(
			`/animals/${animalId}/status-logs/${statusLogId}`,
			data
		),

	deleteStatusLog: (animalId: number, statusLogId: number) =>
		apiClient.delete(`/animals/${animalId}/status-logs/${statusLogId}`),
	deleteStatusLogPhoto: (photoId: number) =>
		apiClient
			.delete(`/animals/status-logs/photos/${photoId}`)
			.then(r => r.data),

	deleteStatusLogDocument: (documentId: number) =>
		apiClient
			.delete(`/animals/status-logs/documents/${documentId}`)
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

	getAttributeHistory: (animalId: number) =>
		apiClient
			.get<AttributeHistoryResponse[]>(
				`/animals/${animalId}/attributes/history`
			)
			.then(r => r.data),

	addStatusLog: (animalId: number, data: { notes: string; logDate: string }) =>
		apiClient.post<StatusLogResponse>(`/animals/${animalId}/status-logs`, data),

	addStatusLogPhoto: (animalId: number, statusLogId: number, file: File) => {
		const formData = new FormData()
		formData.append('file', file)
		return apiClient.post(
			`/animals/${animalId}/status-logs/${statusLogId}/media/photos`,
			formData,
			{ headers: { 'Content-Type': 'multipart/form-data' } }
		)
	},

	addStatusLogDocument: (
		animalId: number,
		statusLogId: number,
		file: File,
		type: string
	) => {
		const formData = new FormData()
		formData.append('file', file)
		formData.append('type', type)
		return apiClient.post<string>(
			`/animals/${animalId}/status-logs/${statusLogId}/media/documents`,
			formData,
			{ headers: { 'Content-Type': 'multipart/form-data' } }
		)
	},

	addStatusLogAttribute: (
		animalId: number,
		statusLogId: number,
		data: { name: string; value: string }
	) =>
		apiClient.post<AttributeResponse>(
			`/animals/${animalId}/status-logs/${statusLogId}/attributes`,
			data
		),

	updateStatusLogAttribute: (
		animalId: number,
		statusLogId: number,
		attributeId: number,
		data: { name?: string; value?: string }
	) =>
		apiClient.patch<AttributeResponse>(
			`/animals/${animalId}/status-logs/${statusLogId}/attributes/${attributeId}`,
			data
		),

	getAnalytics: (animalId: number) =>
		apiClient.get(`/animals/${animalId}/analytics`),
}
