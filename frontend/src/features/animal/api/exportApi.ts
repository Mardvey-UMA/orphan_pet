import { apiClient } from '../../../api/client'

export const exportApi = {
	exportAnimalToPdf: async (animalId: number) => {
		const response = await apiClient.get(
			`/animals/${animalId}/analytics/export/pdf`,
			{
				responseType: 'blob',
				headers: {
					Accept: 'application/pdf',
				},
			}
		)
		return response.data
	},
}
