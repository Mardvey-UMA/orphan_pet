import { apiClient } from '../../../api/client'

// export const exportApi = {
// 	exportAnimalToPdf: (animalId: number) =>
// 		apiClient
// 			.get(`/animals/${animalId}/export/pdf`, {
// 				responseType: 'blob',
// 				headers: {
// 					Accept: 'application/pdf',
// 				},
// 			})
// 			.then(response => {
// 				const url = window.URL.createObjectURL(new Blob([response.data]))
// 				const link = document.createElement('a')
// 				link.href = url
// 				link.setAttribute('download', `animal_${animalId}_report.pdf`)
// 				document.body.appendChild(link)
// 				link.click()
// 				document.body.removeChild(link)
// 				return response
// 			}),
// }
export const exportApi = {
	exportAnimalToPdf: async (animalId: number) => {
		const response = await apiClient.get(`/animals/${animalId}/export/pdf`, {
			responseType: 'blob',
			headers: {
				Accept: 'application/pdf',
			},
		})
		return response.data // Возвращаем только данные (Blob)
	},
}
