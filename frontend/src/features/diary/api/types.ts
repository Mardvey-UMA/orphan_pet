import { ParameterChangeResponse } from '@/features/animal/api/types'

export interface StatusLogCreateRequest {
	notes?: string
	logDate: string
	massChange?: number
	heightChange?: number
	temperatureChange?: number
	activityLevelChange?: number
	appetiteLevelChange?: number
}

export interface StatusLogUpdateRequest {
	notes?: string
	logDate?: string
}

export interface StatusLogUpdateRequest {
	notes?: string
	logDate?: string
}

export interface StatusLogResponse {
	id: number
	logDate: string
	notes?: string
	massChange?: number
	heightChange?: number
	temperatureChange?: number
	activityLevelChange?: number
	appetiteLevelChange?: number
	photos: string[]
	documents: string[]
	parameterChanges: ParameterChangeResponse[]
}

export interface ParameterStats {
	minValue?: number
	maxValue?: number
	avgValue?: number
	firstValue?: number
	lastValue?: number
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

export type ParameterType =
	| 'mass'
	| 'height'
	| 'temperature'
	| 'activityLevel'
	| 'appetiteLevel'

export interface ParameterChange {
	parameter: ParameterType
	oldValue: number
	newValue: number
	changedAt: Date
	changedBy: string
}
