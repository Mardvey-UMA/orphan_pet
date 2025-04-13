import {
	AttributeRequest,
	AttributeResponse,
	DocumentResponse,
} from '../../animal/api/types'

export interface StatusLogCreateRequest {
	notes?: string
	logDate?: string // ISO date
	attributes?: AttributeRequest[]
}

export interface StatusLogUpdateRequest {
	notes?: string
	logDate?: string
}

export interface StatusLogResponse {
	id: number
	logDate: string
	notes?: string
	photos: string[] // objectKeys
	documents: DocumentResponse[]
	attributes: AttributeResponse[]
}

export interface AttributeHistoryResponse {
	attributeName: string
	oldValue?: string
	changedAt: string
	changedBy: string
}

export interface StatusLogWithHistory extends StatusLogResponse {
	attributeHistory: AttributeHistoryResponse[]
}
