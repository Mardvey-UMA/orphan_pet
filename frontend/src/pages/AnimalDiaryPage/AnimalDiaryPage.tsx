import { FileUpload } from '@/components/FileUpload/FileUpload'
import { useAnimal } from '@/features/animal/hooks/useAnimal'
import { diaryApi } from '@/features/diary/api/diaryApi'
import { StatusLogResponse } from '@/features/diary/api/types'
import { PlusOutlined, UploadOutlined } from '@ant-design/icons'
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

	const { animal } = useAnimal(Number(id))
	const { data: diaryEntriesResponse } = useQuery({
		queryKey: ['animalDiary', id],
		queryFn: () => diaryApi.getStatusLogs(Number(id!)),
	})

	// Извлекаем данные из ответа API
	const diaryEntries: StatusLogResponse[] = diaryEntriesResponse?.data || []

	const currentEntry = diaryEntries.find((entry: StatusLogResponse) =>
		dayjs(entry.logDate).isSame(selectedDate, 'day')
	)

	const createEntry = useMutation({
		mutationFn: (notes: string) =>
			diaryApi.createStatusLog(Number(id!), {
				notes,
				logDate: selectedDate.format('YYYY-MM-DD'),
			}),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animalDiary', id] })
			message.success('Запись успешно создана')
		},
		onError: () => {
			message.error('Ошибка при создании записи')
		},
	})

	const updateEntry = useMutation({
		mutationFn: (notes: string) => {
			if (!currentEntry?.id) {
				throw new Error('Entry ID is missing')
			}
			return diaryApi.updateStatusLog(Number(id!), currentEntry.id, { notes })
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animalDiary', id] })
			message.success('Запись успешно обновлена')
		},
		onError: () => {
			message.error('Ошибка при обновлении записи')
		},
	})

	const addPhoto = useMutation({
		mutationFn: (file: File) => {
			if (!currentEntry?.id) {
				throw new Error('Entry ID is missing')
			}
			return diaryApi.addStatusLogPhoto(Number(id!), currentEntry.id, file)
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['animalDiary', id] })
			message.success('Фото успешно добавлено')
		},
		onError: () => {
			message.error('Ошибка при загрузке фото')
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

			<div className={styles.calendarSection}>
				<Calendar
					value={selectedDate}
					onChange={setSelectedDate}
					onPanelChange={setSelectedDate}
					dateCellRender={dateCellRender}
					className={styles.calendar}
				/>
			</div>

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

							<div className={styles.actions}>
								<Button
									type='primary'
									onClick={handleSave}
									loading={updateEntry.isPending || createEntry.isPending}
								>
									Сохранить
								</Button>
							</div>
						</Form>

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
								{currentEntry.photos.map((photo: string, index: number) => (
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
	)
}
