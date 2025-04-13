import { useQuery } from '@tanstack/react-query'
import { useToken } from '../../auth/hooks/useToken'
import { analyticsApi } from '../api/analyticsApi'

export const useUserAnimals = () => {
	const { getAccessToken } = useToken()

	return useQuery({
		queryKey: ['userAnimals'],
		queryFn: analyticsApi.getUserAnimals,
		enabled: !!getAccessToken(),
		staleTime: 5 * 60 * 1000, // 5 минут
	})
}
