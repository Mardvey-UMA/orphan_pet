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
	attributes: AttributeResponse[]
	photos: string[]
	documents: string[]
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
