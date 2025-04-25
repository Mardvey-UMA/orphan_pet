import { useQuery } from '@tanstack/react-query'
import { analyticsApi } from '../api/analyticsApi'

export const useAnalytics = (animalId?: number) => {
	return useQuery({
		queryKey: ['animalAnalytics', animalId],
		queryFn: () => analyticsApi.getAnimalAnalytics(animalId!),
		enabled: !!animalId,
		select: data => {
			return data.map(item => ({
				...item,
				changes: item.changes
					.map(change => ({
						value: parseFloat(change.newValue),
						date: new Date(change.changedAt),
						changedBy: change.changedBy,
					}))
					.sort((a, b) => a.date.getTime() - b.date.getTime()),
			}))
		},
	})
}
