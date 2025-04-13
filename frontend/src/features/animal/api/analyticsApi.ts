import { apiClient } from '../../../api/client'
import { AnimalResponse } from './types'

export interface AnimalAnalyticsResponse {
	attributeName: string
	changes: {
		date: string
		value: string
		changedBy: string
	}[]
	stats?: {
		minValue: string
		maxValue: string
		avgValue: number
	}
}

export const analyticsApi = {
	getAnimalAnalytics: (animalId: number) =>
		apiClient
			.get<AnimalAnalyticsResponse[]>(`/animals/${animalId}/analytics`)
			.then(r => r.data),

	getUserAnimals: () =>
		apiClient.get<AnimalResponse[]>('/users/me/animals').then(r => r.data),
}
