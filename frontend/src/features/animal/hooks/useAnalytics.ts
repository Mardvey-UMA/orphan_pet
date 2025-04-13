import { useQuery } from '@tanstack/react-query'
import { useToken } from '../../auth/hooks/useToken'
import { analyticsApi } from '../api/analyticsApi'

export const useAnalytics = (animalId?: number) => {
	const { getAccessToken } = useToken()

	return useQuery({
		queryKey: ['animalAnalytics', animalId],
		queryFn: () => analyticsApi.getAnimalAnalytics(animalId!),
		enabled: !!animalId && !!getAccessToken(),
		select: data =>
			data.map(item => ({
				...item,
				changes: item.changes.map(change => ({
					...change,
					date: new Date(change.date).toLocaleDateString(),
				})),
			})),
	})
}
