// Обновление данных пользователя
export interface UserUpdateRequest {
	firstName?: string
	lastName?: string
	city?: string
	aboutMe?: string
}

export interface UserResponse {
	username: string
	firstName?: string
	lastName?: string
	city?: string
	aboutMe?: string
	photoUrl?: string
}

// Загрузка фото
export interface S3FileResponse {
	objectKey: string
	presignedUrl: string
}
