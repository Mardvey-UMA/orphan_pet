import { AddAnimalModal } from '@/components/AddAnimalModal/AddAnimalModal'
import { UserAvatarUpload } from '@/components/UserAvatarUpload/UserAvatarUpload'
import { useUserAnimals } from '@/features/animal/hooks/useUserAnimals'
import { useUser } from '@/features/user/hooks/useUser'
import { PlusOutlined } from '@ant-design/icons'
import { Button, Card, Form, Input, message } from 'antd'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import styles from './ProfilePage.module.scss'

interface ProfileFormValues {
	firstName?: string
	lastName?: string
	city?: string
	aboutMe?: string
}

export default function ProfilePage() {
	const [isModalOpen, setIsModalOpen] = useState(false)
	const { currentUser, isPending, updateUser } = useUser()
	const {
		data: animals = [],
		isPending: isAnimalsLoading,
		refetch,
	} = useUserAnimals()
	const [form] = Form.useForm<ProfileFormValues>()
	const navigate = useNavigate()
	useEffect(() => {
		if (currentUser) {
			form.setFieldsValue({
				firstName: currentUser.firstName,
				lastName: currentUser.lastName,
				city: currentUser.city,
				aboutMe: currentUser.aboutMe,
			})
		}
	}, [currentUser, form])

	const onFinish = async (values: ProfileFormValues) => {
		try {
			await updateUser(values)
			message.success('Профиль обновлён!')
		} catch (error) {
			message.error('Ошибка обновления профиля')
			console.error('Update error:', error)
		}
	}

	const handleAnimalCreated = () => {
		refetch()
		setIsModalOpen(false)
	}

	if (isPending) return <div>Загрузка профиля...</div>

	return (
		<div className={styles.profileContainer}>
			<h1>Профиль</h1>

			<div className={styles.profileSection}>
				<UserAvatarUpload currentPhotoUrl={currentUser?.photoUrl} />

				<Form form={form} onFinish={onFinish} layout='vertical'>
					<Form.Item name='firstName' label='Имя'>
						<Input />
					</Form.Item>
					<Form.Item name='lastName' label='Фамилия'>
						<Input />
					</Form.Item>
					<Form.Item name='city' label='Город'>
						<Input />
					</Form.Item>
					<Form.Item name='aboutMe' label='О себе'>
						<Input.TextArea rows={4} />
					</Form.Item>
					<Button type='primary' htmlType='submit'>
						Сохранить
					</Button>
				</Form>
			</div>

			<div className={styles.animalsSection}>
				<div className={styles.animalsHeader}>
					<h2>Мои животные</h2>
					<Button
						type='primary'
						icon={<PlusOutlined />}
						onClick={() => setIsModalOpen(true)}
					>
						Добавить
					</Button>
				</div>

				{isAnimalsLoading ? (
					<div>Загрузка животных...</div>
				) : (
					<div className={styles.animalsGrid}>
						{animals.map(animal => (
							<Card
								key={animal.id}
								hoverable
								onClick={() => navigate(`/animals/${animal.id}`)}
								cover={
									animal.photos?.[0] ? (
										<img
											alt={animal.name}
											src={animal.photos[0]}
											className={styles.animalImage}
										/>
									) : (
										<div className={styles.noImage}>Нет фото</div>
									)
								}
							>
								<Card.Meta title={animal.name} />
							</Card>
						))}
					</div>
				)}
			</div>

			<AddAnimalModal
				open={isModalOpen}
				onClose={() => setIsModalOpen(false)}
				onAnimalCreated={handleAnimalCreated}
			/>
		</div>
	)
}
