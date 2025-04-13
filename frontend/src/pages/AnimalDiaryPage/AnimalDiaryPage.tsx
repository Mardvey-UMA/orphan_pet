import { FileUpload } from '@/components/FileUpload/FileUpload'
import { animalApi } from '@/features/animal/api/animalApi'
import { AnimalResponse, DocumentResponse } from '@/features/animal/api/types'
import { diaryApi } from '@/features/diary/api/diaryApi'
import { StatusLogResponse } from '@/features/diary/api/types'
import { DeleteOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
	Button,
	Calendar,
	Card,
	Form,
	Image,
	Input,
	message,
	Modal,
	Typography,
} from 'antd'
import dayjs, { Dayjs } from 'dayjs'
import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import styles from './AnimalDiaryPage.module.scss'

const { Title, Text } = Typography

export const AnimalDiaryPage = () => {
	const { id } = useParams<{ id: string }>()
	const navigate = useNavigate()
	const queryClient = useQueryClient()
	const [form] = Form.useForm()
	const [selectedDate, setSelectedDate] = useState<Dayjs>(dayjs())
	const [isPhotoModalOpen, setIsPhotoModalOpen] = useState(false)
	const [selectedPhotoIndex, setSelectedPhotoIndex] = useState(0)
	//const [newAttribute, setNewAttribute] = useState({ name: '', value: '' })
	const [newAttribute, setNewAttribute] = useState<{
		name: string
		value: string
	}>({ name: '', value: '' })

	const { data: animal } = useQuery<AnimalResponse>({
		queryKey: ['animal', id],
		queryFn: () => animalApi.getAnimal(Number(id!)),
	})

	const { data: diaryEntriesResponse } = useQuery({
		queryKey: ['animalDiary', id],
		queryFn: () => diaryApi.getStatusLogs(Number(id!)),
	})

	const diaryEntries: StatusLogResponse[] = diaryEntriesResponse?.data || []
	const currentEntry = diaryEntries.find((entry: StatusLogResponse) =>
		dayjs(entry.logDate).isSame(selectedDate, 'day')
	)

	const photos = currentEntry?.photos || []
	const documents = currentEntry?.documents || []
	//const attributes = currentEntry?.attributes || []

	// Мутации
	const createEntry = useMutation({
		mutationFn: (notes: string) =>
			diaryApi.createStatusLog(Number(id!), {
				notes,
				logDate: selectedDate.format('YYYY-MM-DD'),
			}),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animalDiary', id] })
			form.resetFields()
			message.success('Запись создана')
		},
	})

	const updateEntry = useMutation({
		mutationFn: (notes: string) => {
			if (!currentEntry?.id) throw new Error('Entry ID is missing')
			return diaryApi.updateStatusLog(Number(id!), currentEntry.id, { notes })
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animalDiary', id] })
			message.success('Запись обновлена')
		},
	})

	const addPhoto = useMutation({
		mutationFn: (file: File) => {
			if (!currentEntry?.id) throw new Error('Entry ID is missing')
			return diaryApi.addStatusLogPhoto(Number(id!), currentEntry.id, file)
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animalDiary', id] })
			message.success('Фото добавлено')
		},
	})

	const addDocument = useMutation({
		mutationFn: (file: File) => {
			if (!currentEntry?.id) throw new Error('Entry ID is missing')
			return diaryApi.addStatusLogDocument(
				Number(id!),
				currentEntry.id,
				file,
				'OTHER'
			)
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animalDiary', id] })
			message.success('Документ добавлен')
		},
	})

	// const addAttribute = useMutation({
	// 	mutationFn: (data: { name: string; value: string }) => {
	// 		if (!currentEntry?.id) throw new Error('Entry ID is missing')
	// 		return diaryApi.addStatusLogAttribute(Number(id!), currentEntry.id, data)
	// 	},
	// 	onSuccess: () => {
	// 		queryClient.invalidateQueries({ queryKey: ['animalDiary', id] })
	// 		setNewAttribute({ name: '', value: '' })
	// 		message.success('Атрибут добавлен')
	// 	},
	// })

	// const updateAttribute = useMutation({
	// 	mutationFn: ({
	// 		id: attrId,
	// 		name,
	// 		value,
	// 	}: {
	// 		id: number
	// 		name: string
	// 		value: string
	// 	}) => {
	// 		if (!currentEntry?.id) throw new Error('Entry ID is missing')
	// 		return diaryApi.updateStatusLogAttribute(
	// 			Number(id!),
	// 			currentEntry.id,
	// 			attrId,
	// 			{ name, value }
	// 		)
	// 	},
	// 	onSuccess: () => {
	// 		queryClient.invalidateQueries({ queryKey: ['animalDiary', id] })
	// 		message.success('Атрибут обновлен')
	// 	},
	// })

	// const deleteAttribute = useMutation({
	// 	mutationFn: (attrId: number) => {
	// 		if (!currentEntry?.id) throw new Error('Entry ID is missing')
	// 		return diaryApi.deleteStatusLogAttribute(
	// 			Number(id!),
	// 			currentEntry.id,
	// 			attrId
	// 		)
	// 	},
	// 	onSuccess: () => {
	// 		queryClient.invalidateQueries({ queryKey: ['animalDiary', id] })
	// 		message.success('Атрибут удален')
	// 	},
	// })
	// Мутации для работы с атрибутами животного
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
	const handleSave = () => {
		const notes = form.getFieldValue('notes')
		if (currentEntry) {
			updateEntry.mutate(notes)
		} else {
			createEntry.mutate(notes || 'Новая запись')
		}
	}

	const handleAddAttribute = () => {
		if (!newAttribute.name || !newAttribute.value) {
			message.warning('Заполните название и значение атрибута')
			return
		}
		addAttribute.mutate(newAttribute)
	}

	const dateCellRender = (value: Dayjs) => {
		const hasEntry = diaryEntries.some((entry: StatusLogResponse) =>
			dayjs(entry.logDate).isSame(value, 'day')
		)
		return hasEntry ? <div className={styles.calendarDayWithEntry} /> : null
	}

	if (!animal) return <div>Животное не найдено</div>

	return (
		<div className={styles.container}>
			<Button
				onClick={() => navigate(`/animals/${id}`)}
				className={styles.backButton}
			>
				Назад к животному
			</Button>

			<Title level={2}>Дневник: {animal.name}</Title>
			<div className={styles.layoutContainer}>
				<div className={styles.calendarContainer}>
					<Calendar
						value={selectedDate}
						onChange={setSelectedDate}
						onPanelChange={setSelectedDate}
						dateCellRender={dateCellRender}
						className={styles.calendar}
					/>
				</div>
				<div className={styles.entryContainer}>
					<Card
						title={`Запись за ${selectedDate.format('DD.MM.YYYY')}`}
						className={styles.entryCard}
					>
						{currentEntry ? (
							<>
								<Form
									form={form}
									initialValues={{ notes: currentEntry.notes }}
									layout='vertical'
								>
									<Form.Item name='notes' label='Заметки'>
										<Input.TextArea
											rows={4}
											placeholder='Опишите состояние животного...'
										/>
									</Form.Item>

									{/* Секция атрибутов */}
									{/* Атрибуты животного */}
									<Card
										title={
											<div className={styles.cardTitleWithButton}>
												<span>Атрибуты животного</span>
											</div>
										}
										className={styles.section}
									>
										<div className={styles.newAttribute}>
											<Input
												placeholder='Название атрибута'
												value={newAttribute.name}
												onChange={e =>
													setNewAttribute({
														...newAttribute,
														name: e.target.value,
													})
												}
											/>
											<Input
												placeholder='Значение'
												value={newAttribute.value}
												onChange={e =>
													setNewAttribute({
														...newAttribute,
														value: e.target.value,
													})
												}
											/>
											<Button
												type='primary'
												icon={<PlusOutlined />}
												onClick={handleAddAttribute}
												//loading={addAttribute.isPending}
											>
												Добавить
											</Button>
											<Text type='secondary' className={styles.tip}>
												Примеры: Порода, Цвет, Особенности, Аллергии
											</Text>
										</div>

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

									{/* Секция фото */}
									<div className={styles.photosSection}>
										<h3>Фото</h3>
										<FileUpload
											beforeUpload={(file: File) => {
												addPhoto.mutate(file)
												return false
											}}
											accept='image/*'
											showUploadList={false}
											buttonText='Добавить фото'
											buttonIcon={<UploadOutlined />}
										/>

										<div className={styles.photosGrid}>
											{photos.map((photo: string, index: number) => (
												<div key={index} className={styles.photoWrapper}>
													<Image
														src={photo}
														width={100}
														height={100}
														preview={{ visible: false }}
														onClick={() => {
															setSelectedPhotoIndex(index)
															setIsPhotoModalOpen(true)
														}}
													/>
												</div>
											))}
										</div>
									</div>

									{/* Секция документов */}
									<div className={styles.documentsSection}>
										<h3>Документы</h3>
										<FileUpload
											beforeUpload={(file: File) => {
												addDocument.mutate(file)
												return false
											}}
											accept='.pdf,.doc,.docx'
											showUploadList={false}
											buttonText='Добавить документ'
											buttonIcon={<UploadOutlined />}
										/>

										<div className={styles.documentsList}>
											{documents.map((doc: DocumentResponse, index: number) => (
												<div key={index} className={styles.documentItem}>
													<a
														href={doc.url}
														target='_blank'
														rel='noopener noreferrer'
													>
														Документ {index + 1}
													</a>
												</div>
											))}
										</div>
									</div>

									{/* Кнопка сохранения внизу */}
									<div className={styles.saveButton}>
										<Button
											type='primary'
											onClick={handleSave}
											loading={updateEntry.isPending || createEntry.isPending}
											block
										>
											Сохранить запись
										</Button>
									</div>
								</Form>
							</>
						) : (
							<div className={styles.noEntry}>
								<Text type='secondary'>Запись на эту дату отсутствует</Text>
								<Button
									type='primary'
									icon={<PlusOutlined />}
									onClick={() => createEntry.mutate('Новая запись')}
									loading={createEntry.isPending}
								>
									Создать запись
								</Button>
							</div>
						)}
					</Card>

					{/* Модальное окно просмотра фото */}
					<Modal
						open={isPhotoModalOpen}
						footer={null}
						onCancel={() => setIsPhotoModalOpen(false)}
						width='80vw'
						bodyStyle={{ padding: 0 }}
					>
						{currentEntry && currentEntry?.photos?.length > 0 && (
							<div className={styles.photoModal}>
								<Image
									src={currentEntry.photos[selectedPhotoIndex]}
									style={{ maxHeight: '80vh', objectFit: 'contain' }}
								/>
								{currentEntry.photos.length > 1 && (
									<div className={styles.photoNavigation}>
										<Button
											onClick={() =>
												setSelectedPhotoIndex(
													prev =>
														(prev - 1 + currentEntry.photos.length) %
														currentEntry.photos.length
												)
											}
										>
											Назад
										</Button>
										<span>
											{selectedPhotoIndex + 1} / {currentEntry.photos.length}
										</span>
										<Button
											onClick={() =>
												setSelectedPhotoIndex(
													prev => (prev + 1) % currentEntry.photos.length
												)
											}
										>
											Вперед
										</Button>
									</div>
								)}
							</div>
						)}
					</Modal>
				</div>
			</div>
		</div>
	)
}
