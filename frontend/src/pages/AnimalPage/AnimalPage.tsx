import { FileUpload } from '@/components/FileUpload/FileUpload'
import { animalApi } from '@/features/animal/api/animalApi'
import { exportApi } from '@/features/animal/api/exportApi'
import {
	AnimalResponse,
	AnimalUpdateRequest,
} from '@/features/animal/api/types'
import {
	BookOutlined,
	DeleteOutlined,
	EditOutlined,
	EyeOutlined,
	FilePdfOutlined,
	PlusOutlined,
	UploadOutlined,
} from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
	Button,
	Card,
	DatePicker,
	Form,
	Image,
	Input,
	InputNumber,
	message,
	Modal,
	Space,
	Typography,
} from 'antd'
import dayjs from 'dayjs'
import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import styles from './AnimalPage.module.scss'

const { Title, Text } = Typography

export const AnimalPage = () => {
	const { id } = useParams<{ id: string }>()
	const navigate = useNavigate()
	const queryClient = useQueryClient()
	const [form] = Form.useForm()
	const [isEditing, setIsEditing] = useState(false)
	const [isPhotoModalOpen, setIsPhotoModalOpen] = useState(false)
	const [selectedPhotoIndex, setSelectedPhotoIndex] = useState(0)
	const [newAttribute, setNewAttribute] = useState<{
		name: string
		value: string
	}>({ name: '', value: '' })

	// Получение данных животного
	const { data: animal, isPending } = useQuery<AnimalResponse>({
		queryKey: ['animal', id],
		queryFn: () => animalApi.getAnimal(Number(id!)),
	})

	// Мутации
	const updateAnimal = useMutation({
		mutationFn: (data: AnimalUpdateRequest) =>
			animalApi.updateAnimal(Number(id!), data),
		onSuccess: () => {
			message.success('Данные обновлены!')
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
			setIsEditing(false)
		},
	})

	const addPhoto = useMutation({
		mutationFn: (file: File) => animalApi.uploadAnimalPhoto(Number(id!), file),
		onSuccess: () => {
			message.success('Фото добавлено!')
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
		},
	})

	const deletePhoto = useMutation({
		mutationFn: (photoUrl: string) => animalApi.deleteAnimalPhoto(photoUrl),
		onSuccess: () => {
			message.success('Фото удалено!')
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
		},
	})

	const addDocument = useMutation({
		mutationFn: ({ file, type }: { file: File; type: string }) =>
			animalApi.uploadAnimalDocument(Number(id!), file, type),
		onSuccess: () => {
			message.success('Документ добавлен!')
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
		},
	})

	const deleteDocument = useMutation({
		mutationFn: (documentUrl: string) =>
			animalApi.deleteAnimalDocument(documentUrl),
		onSuccess: () => {
			message.success('Документ удален!')
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
		},
	})

	const addAttribute = useMutation({
		mutationFn: (data: { name: string; value: string }) =>
			animalApi.addAttribute(Number(id!), data),
		onSuccess: () => {
			message.success('Атрибут добавлен!')
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
			setNewAttribute({ name: '', value: '' })
		},
	})

	const deleteAttribute = useMutation({
		mutationFn: (attributeId: number) =>
			animalApi.deleteAttribute(Number(id!), attributeId),
		onSuccess: () => {
			message.success('Атрибут удален!')
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
		},
	})
	const exportToPdf = useMutation({
		mutationFn: () => exportApi.exportAnimalToPdf(Number(id!)),
		onSuccess: (pdfBlob: Blob) => {
			const url = window.URL.createObjectURL(pdfBlob)
			const link = document.createElement('a')
			link.href = url
			link.setAttribute('download', `animal_${animal?.name}_report.pdf`)
			document.body.appendChild(link)
			link.click()
			document.body.removeChild(link)

			// Освобождаем память
			setTimeout(() => {
				window.URL.revokeObjectURL(url)
			}, 100)
		},
		onError: error => {
			console.error('Export failed:', error)
			message.error('Не удалось экспортировать в PDF')
		},
	})
	// const exportToPdf = useMutation({
	// 	mutationFn: () => exportApi.exportAnimalToPdf(Number(id!)),
	// 	onSuccess: pdfData => {
	// 		const blob = new Blob([pdfData], { type: 'application/pdf' })
	// 		const url = window.URL.createObjectURL(blob)
	// 		const link = document.createElement('a')
	// 		link.href = url
	// 		link.setAttribute('download', `animal_${animal?.name}_report.pdf`)
	// 		document.body.appendChild(link)
	// 		link.click()
	// 		document.body.removeChild(link)
	// 	},
	// })
	const handleSave = () => {
		const values = form.getFieldsValue()
		updateAnimal.mutate({
			...values,
			birthDate: values.birthDate?.format('YYYY-MM-DD'),
		})
	}

	const handleAddAttribute = () => {
		if (!newAttribute.name || !newAttribute.value) {
			message.warning('Заполните название и значение атрибута')
			return
		}
		addAttribute.mutate(newAttribute)
	}

	const handleDeletePhoto = (photoUrl: string, e: React.MouseEvent) => {
		e.stopPropagation()
		deletePhoto.mutate(photoUrl)
	}

	const handleDeleteDocument = (documentUrl: string) => {
		deleteDocument.mutate(documentUrl)
	}

	if (isPending) return <div>Загрузка...</div>
	if (!animal) return <div>Животное не найдено</div>

	return (
		<div className={styles.container}>
			<Button onClick={() => navigate(-1)} className={styles.backButton}>
				Назад к списку
			</Button>
			<Space>
				<Button
					type='default'
					icon={<BookOutlined />}
					onClick={() => navigate(`/animals/${id}/diary`)}
				>
					Дневник
				</Button>
				<Button
					type='primary'
					icon={<FilePdfOutlined />}
					onClick={() => exportToPdf.mutate()}
					loading={exportToPdf.isPending}
				>
					Экспорт в PDF
				</Button>
			</Space>
			<Title level={2}>{animal.name}</Title>

			<div className={styles.sectionsContainer}>
				{/* Основная информация */}
				<Card
					title='Основная информация'
					extra={
						isEditing ? null : (
							<EditOutlined onClick={() => setIsEditing(true)} />
						)
					}
					className={styles.section}
				>
					{isEditing ? (
						<Form
							form={form}
							initialValues={{
								name: animal.name,
								description: animal.description,
								birthDate: animal.birthDate ? dayjs(animal.birthDate) : null,
								mass: animal.mass,
								height: animal.height,
								temperature: animal.temperature,
								activityLevel: animal.activityLevel,
								appetiteLevel: animal.appetiteLevel,
							}}
							layout='vertical'
						>
							<Form.Item name='name' label='Имя'>
								<Input />
							</Form.Item>
							<Form.Item name='description' label='Описание'>
								<Input.TextArea rows={3} />
							</Form.Item>
							<Form.Item name='birthDate' label='Дата рождения'>
								<DatePicker style={{ width: '100%' }} />
							</Form.Item>
							<Form.Item name='mass' label='Вес (кг)'>
								<Input type='number' />
							</Form.Item>
							<Form.Item name='height' label='Рост (см)'>
								<Input type='number' step='0.1' />
							</Form.Item>

							<Form.Item name='temperature' label='Температура (°C)'>
								<Input type='number' step='0.1' />
							</Form.Item>

							<Form.Item name='activityLevel' label='Уровень активности (1-10)'>
								<InputNumber min={1} max={10} />
							</Form.Item>

							<Form.Item name='appetiteLevel' label='Уровень аппетита (1-10)'>
								<InputNumber min={1} max={10} />
							</Form.Item>
							<Space>
								<Button type='primary' onClick={handleSave}>
									Сохранить
								</Button>
								<Button onClick={() => setIsEditing(false)}>Отмена</Button>
							</Space>
						</Form>
					) : (
						<>
							<p>
								<Text strong>Описание:</Text> {animal.description || '—'}
							</p>
							<p>
								<Text strong>Дата рождения:</Text> {animal.birthDate || '—'}
							</p>
							<p>
								<Text strong>Вес:</Text>{' '}
								{animal.mass ? `${animal.mass} кг` : '—'}
							</p>
						</>
					)}
				</Card>

				{/* Атрибуты */}
				<Card
					title={
						<div className={styles.cardTitleWithButton}>
							<span>Атрибуты</span>
						</div>
					}
					className={styles.section}
				>
					{newAttribute.name !== undefined && (
						<div className={styles.newAttribute}>
							<Input
								placeholder='Название атрибута'
								value={newAttribute.name}
								onChange={e =>
									setNewAttribute({ ...newAttribute, name: e.target.value })
								}
							/>
							<Input
								placeholder='Значение'
								value={newAttribute.value}
								onChange={e =>
									setNewAttribute({ ...newAttribute, value: e.target.value })
								}
							/>
							<Button
								type='primary'
								icon={<PlusOutlined />}
								onClick={handleAddAttribute}
							>
								Добавить
							</Button>
							<Text type='secondary' className={styles.tip}>
								Примеры: Порода, Цвет, Особенности, Аллергии
							</Text>
						</div>
					)}

					<div className={styles.attributesList}>
						{animal.attributes.map(attr => (
							<div key={attr.id} className={styles.attributeItem}>
								<div className={styles.attributeContent}>
									<Text strong>{attr.name}:</Text> {attr.value || '—'}
								</div>
								<Button
									type='text'
									danger
									icon={<DeleteOutlined />}
									onClick={() => deleteAttribute.mutate(attr.id)}
									loading={deleteAttribute.isPending}
								/>
							</div>
						))}
						{animal.attributes.length === 0 && (
							<Text type='secondary'>Нет добавленных атрибутов</Text>
						)}
					</div>
				</Card>

				{/* Фотографии */}
				<Card title='Фотографии' className={styles.section}>
					<FileUpload
						beforeUpload={(file: File) => {
							addPhoto.mutate(file)
							return false
						}}
						accept='image/*'
						showUploadList={false}
						buttonText={'Добавить фото +'}
						buttonIcon={undefined}
					>
						<Button
							type='text'
							icon={<UploadOutlined className={styles.darkIcon} />}
						>
							Добавить фото
						</Button>
					</FileUpload>

					<div className={styles.photosGrid}>
						{animal.photos.map((photo, index) => (
							<div key={index} className={styles.photoWrapper}>
								<Image
									src={photo}
									alt={`Фото ${animal.name}`}
									width={150}
									height={150}
									preview={{
										visible: false,
										onVisibleChange: visible => {
											if (visible) {
												setSelectedPhotoIndex(index)
												setIsPhotoModalOpen(true)
											}
										},
									}}
								/>
								<div className={styles.photoActions}>
									<EyeOutlined
										className={styles.viewIcon}
										onClick={() => {
											setSelectedPhotoIndex(index)
											setIsPhotoModalOpen(true)
										}}
									/>
									<DeleteOutlined
										className={styles.deleteIcon}
										onClick={e => handleDeletePhoto(photo, e)}
									/>
								</div>
							</div>
						))}
						{animal.photos.length === 0 && (
							<Text type='secondary'>Нет добавленных фото</Text>
						)}
					</div>
				</Card>

				{/* Документы */}
				<Card title='Документы' className={styles.section}>
					<FileUpload
						beforeUpload={(file: File) => {
							addDocument.mutate({ file, type: 'OTHER' })
							return false
						}}
						accept='.pdf,.doc,.docx'
						showUploadList={false}
						buttonText={'Добавить документ +'}
						buttonIcon={undefined}
					>
						<Button
							type='text'
							icon={<UploadOutlined className={styles.darkIcon} />}
						>
							Добавить документ
						</Button>
					</FileUpload>

					<div className={styles.documentsList}>
						{animal.documents.map((doc, index) => (
							<div key={index} className={styles.documentItem}>
								<a href={doc} target='_blank' rel='noopener noreferrer'>
									Документ {index + 1}
								</a>
								<Button
									type='text'
									danger
									icon={<DeleteOutlined />}
									onClick={() => handleDeleteDocument(doc)}
									loading={deleteDocument.isPending}
								/>
							</div>
						))}
						{animal.documents.length === 0 && (
							<Text type='secondary'>Нет добавленных документов</Text>
						)}
					</div>
				</Card>
			</div>

			{/* Модальное окно просмотра фото */}
			<Modal
				open={isPhotoModalOpen}
				footer={null}
				onCancel={() => setIsPhotoModalOpen(false)}
				width='80vw'
				bodyStyle={{ padding: 0 }}
			>
				<div className={styles.photoModal}>
					<Image
						src={animal.photos[selectedPhotoIndex]}
						alt={`Фото ${animal.name}`}
						style={{ maxHeight: '80vh', objectFit: 'contain' }}
					/>
					{animal.photos.length > 1 && (
						<div className={styles.photoNavigation}>
							<Button
								onClick={() =>
									setSelectedPhotoIndex(
										prev =>
											(prev - 1 + animal.photos.length) % animal.photos.length
									)
								}
							>
								Назад
							</Button>
							<span>
								{selectedPhotoIndex + 1} / {animal.photos.length}
							</span>
							<Button
								onClick={() =>
									setSelectedPhotoIndex(
										prev => (prev + 1) % animal.photos.length
									)
								}
							>
								Вперед
							</Button>
						</div>
					)}
				</div>
			</Modal>
		</div>
	)
}
