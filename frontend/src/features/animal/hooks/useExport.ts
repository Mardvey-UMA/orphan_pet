import { useMutation } from '@tanstack/react-query'
import { exportApi } from '../api/exportApi'

export const useExport = () => {
	return useMutation({
		mutationFn: exportApi.exportAnimalToPdf,
		onSuccess: () => {
			// Можно добавить уведомление об успешном экспорте
			console.log('PDF exported successfully')
		},
		onError: error => {
			console.error('Export failed:', error)
		},
	})
}
