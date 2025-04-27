export interface BERTPredictionRequest {
	animal_class: string
	description: string
}

export interface BERTPredictionResponse {
	is_dangerous: boolean
}

export interface DeepSeekAnalysisRequest {
	animal_id: number
}

export interface DeepSeekAnalysisResponse {
	advice: string
}
