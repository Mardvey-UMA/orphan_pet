import { useQuery } from '@tanstack/react-query'
import { animalApi } from '../api/animalApi'

export const useUserAnimals = () => {
	return useQuery({
		queryKey: ['userAnimals'],
		queryFn: animalApi.getUserAnimals,
	})
}
