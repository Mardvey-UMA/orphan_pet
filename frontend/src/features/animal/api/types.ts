// Основные типы
export interface AnimalCreateRequest {
	name: string
	description?: string
	birthDate?: string // ISO date string
	mass?: number
	attributes: AttributeRequest[]
}

export interface AnimalUpdateRequest {
	name?: string
	description?: string
	birthDate?: string
	mass?: number
}

export interface AnimalResponse {
	id: number
	name: string
	description?: string
	birthDate?: string
	mass?: number
	attributes: AttributeResponse[]
	photos: string[] // objectKeys
	documents: DocumentResponse[]
	statusLogs: StatusLogResponse[]
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

export interface DocumentResponse {
	id: number
	type: string
	objectKey: string
	documentName?: string
}

export interface StatusLogResponse {
	id: number
	logDate: string
	notes?: string
	photos: string[]
	documents: DocumentResponse[]
}

export interface S3FileResponse {
	objectKey: string
	presignedUrl: string
}
