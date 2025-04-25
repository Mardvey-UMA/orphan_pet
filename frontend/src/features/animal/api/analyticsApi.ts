import { apiClient } from '../../../api/client'
import { AnimalAnalyticsResponse, AnimalResponse } from './types'

export const analyticsApi = {
	getAnimalAnalytics: (animalId: number) =>
		apiClient
			.get<AnimalAnalyticsResponse[]>(`/animals/${animalId}/analytics`)
			.then(r => r.data),

	getUserAnimals: () =>
		apiClient.get<AnimalResponse[]>('/users/me/animals').then(r => r.data),
}
