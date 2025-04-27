import { apiClient } from '@/api/client'
import { useMutation } from '@tanstack/react-query'
import {
	BERTPredictionRequest,
	BERTPredictionResponse,
	DeepSeekAnalysisRequest,
	DeepSeekAnalysisResponse,
} from '../types'

export const useBertAdvice = () =>
	useMutation({
		mutationFn: (data: BERTPredictionRequest) =>
			apiClient.post<BERTPredictionResponse>('/advice/bert/predict', data),
	})

export const useDeepSeekAnalysis = () =>
	useMutation({
		mutationFn: (data: DeepSeekAnalysisRequest) =>
			apiClient.post<DeepSeekAnalysisResponse>(
				'/advice/analysis/analyze',
				data
			),
	})
