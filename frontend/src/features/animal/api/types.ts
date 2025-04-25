export interface AnimalCreateRequest {
	name: string
	description?: string
	birthDate?: string
	mass?: number
	attributes?: AttributeRequest[]
}

export interface AnimalResponse {
	id: number
	name: string
	description?: string
	birthDate?: string
	mass?: number
	height?: number
	temperature?: number
	activityLevel?: number
	appetiteLevel?: number
	photos: string[]
	documents: string[]
	attributes: AttributeResponse[]
}

export interface DocumentResponse {
	id: number
	type: string
	url: string
}

export interface AttributeRequest {
	name: string
	value: string
}

export interface AttributeResponse {
	id: number
	name: string
	value?: string
}
export interface AnimalUpdateRequest {
	name?: string
	description?: string
	birthDate?: string
	mass?: number
	height?: number
	temperature?: number
	activityLevel?: number
	appetiteLevel?: number
}
export interface S3UploadResponse {
	objectKey: string
	presignedUrl: string
}
export interface AnimalPhotoUploadResponse {
	url: string // Прямая ссылка на фото
}

export interface AnimalDocumentUploadResponse {
	url: string // Прямая ссылка на документ
}
export interface ParameterChangeResponse {
	parameterName: string
	oldValue: string
	newValue: string
	changedAt: string
	changedBy: string
}
// export interface AnimalAnalyticsResponse {
// 	parameterName: string
// 	changes: ParameterChangeResponse[]
// 	stats?: {
// 		minValue: string
// 		maxValue: string
// 		avgValue: number
// 	}
// }
export interface AnimalAnalyticsResponse {
	parameterName: string
	changes: {
		oldValue: string
		newValue: string
		changedAt: string
		changedBy: string
	}[]
	stats?: {
		minValue: string
		maxValue: string
		avgValue: number
	}
}
