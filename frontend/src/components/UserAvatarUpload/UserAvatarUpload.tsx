import { useUser } from '@/features/user/hooks/useUser'
import { UploadOutlined } from '@ant-design/icons'
import { Button, Upload, message } from 'antd'
import { RcFile } from 'antd/es/upload'
import styles from './UserAvatarUpload.module.scss'

interface UserAvatarUploadProps {
	currentPhotoUrl?: string
}

export const UserAvatarUpload = ({
	currentPhotoUrl,
}: UserAvatarUploadProps) => {
	const { uploadPhoto, isUploading } = useUser()

	const handleUpload = async (file: RcFile) => {
		try {
			await uploadPhoto(file)
			message.success('Фото успешно обновлено!')
		} catch (error) {
			message.error('Ошибка загрузки фото')
			console.error('Upload error:', error)
		}
		return false // Prevent default upload behavior
	}

	return (
		<div className={styles.uploadContainer}>
			<img
				src={currentPhotoUrl || 'https://placehold.co/150x150?text=No+Photo'}
				alt='User avatar'
				className={styles.avatar}
			/>
			<Upload
				beforeUpload={handleUpload}
				showUploadList={false}
				accept='image/*'
			>
				<Button icon={<UploadOutlined />} loading={isUploading}>
					Обновить фото
				</Button>
			</Upload>
		</div>
	)
}
