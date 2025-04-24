// pages/AnimalDiaryPage/AnimalDiaryPage.tsx
import { FileUpload } from '@/components/FileUpload/FileUpload'
import { ParameterHistory } from '@/components/ParametrHistory/ParameterHistory'
import { animalApi } from '@/features/animal/api/animalApi'
import { diaryApi } from '@/features/diary/api/diaryApi'
import {
	StatusLogCreateRequest,
	StatusLogResponse,
} from '@/features/diary/api/types'
import { BookOutlined, PlusOutlined } from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
	Button,
	Calendar,
	Card,
	Form,
	Image,
	Input,
	InputNumber,
	message,
	Modal,
	Tooltip,
	Typography,
} from 'antd'
import dayjs, { Dayjs } from 'dayjs'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import styles from './AnimalDiaryPage.module.scss'

const { Title } = Typography

export const AnimalDiaryPage = () => {
	const { id } = useParams<{ id: string }>()
	const navigate = useNavigate()
	const queryClient = useQueryClient()
	const [form] = Form.useForm()
	const [selectedDate, setSelectedDate] = useState<Dayjs>(dayjs())
	const [isPhotoModalOpen, setIsPhotoModalOpen] = useState(false)
	const [selectedPhotoIndex, setSelectedPhotoIndex] = useState(0)
	const [filesToUpload, setFilesToUpload] = useState<{
		photos: File[]
		documents: File[]
	}>({ photos: [], documents: [] })

	// Получение данных с явным указанием типов
	const { data: animal } = useQuery({
		queryKey: ['animal', id],
		queryFn: () => animalApi.getAnimal(Number(id!)),
	})

	const { data: diaryEntriesResponse } = useQuery({
		queryKey: ['diaryEntries', id],
		queryFn: () => diaryApi.getStatusLogs(Number(id!)),
	})

	// Обработка данных ответа
	const diaryEntries = useMemo(
		() => diaryEntriesResponse?.data || [],
		[diaryEntriesResponse]
	)

	const currentEntry = useMemo(
		() =>
			diaryEntries.find((e: StatusLogResponse) =>
				dayjs(e.logDate).isSame(selectedDate, 'day')
			),
		[diaryEntries, selectedDate]
	)

	// Инициализация формы
	useEffect(() => {
		if (currentEntry) {
			form.setFieldsValue(currentEntry)
		} else {
			form.resetFields()
		}
	}, [currentEntry, form])

	// Мутации с явными типами
	const createMutation = useMutation({
		mutationFn: (data: StatusLogCreateRequest) =>
			diaryApi.createStatusLog(Number(id!), data),
		onSuccess: async data => {
			await Promise.all([
				...filesToUpload.photos.map(file =>
					diaryApi.addStatusLogPhoto(Number(id!), data.data.id, file)
				),
				...filesToUpload.documents.map(file =>
					diaryApi.addStatusLogDocument(
						Number(id!),
						data.data.id,
						file,
						'MEDICAL'
					)
				),
			])

			queryClient.invalidateQueries({ queryKey: ['diaryEntries', id] })
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
			setFilesToUpload({ photos: [], documents: [] })
			message.success('Запись успешно создана')
		},
	})

	const updateMutation = useMutation({
		mutationFn: (data: StatusLogCreateRequest) => {
			if (!currentEntry) throw new Error('Запись не найдена')
			return diaryApi.updateStatusLog(Number(id!), currentEntry.id, data)
		},
		onSuccess: async () => {
			if (!currentEntry) return
			await Promise.all([
				...filesToUpload.photos.map(file =>
					diaryApi.addStatusLogPhoto(Number(id!), currentEntry.id, file)
				),
				...filesToUpload.documents.map(file =>
					diaryApi.addStatusLogDocument(
						Number(id!),
						currentEntry.id,
						file,
						'MEDICAL'
					)
				),
			])

			queryClient.invalidateQueries({ queryKey: ['diaryEntries', id] })
			queryClient.invalidateQueries({ queryKey: ['animal', id] })
			setFilesToUpload({ photos: [], documents: [] })
			message.success('Запись успешно обновлена')
		},
	})

	const handleDateSelect = (date: Dayjs) => {
		setSelectedDate(date)
		const entry = diaryEntries.find((e: StatusLogResponse) =>
			dayjs(e.logDate).isSame(date, 'day')
		)
		form.resetFields()
		if (entry) form.setFieldsValue(entry)
	}

	const handleSave = async () => {
		try {
			const values = await form.validateFields()

			if (!Object.values(values).some(v => v !== null && v !== undefined)) {
				message.error('Заполните хотя бы одно поле изменений')
				return
			}

			const data = {
				...values,
				logDate: selectedDate.format('YYYY-MM-DD'),
			}

			if (currentEntry) {
				await updateMutation.mutateAsync(data)
			} else {
				await createMutation.mutateAsync(data)
			}
		} catch (error) {
			message.error('Ошибка сохранения записи')
			console.error(error)
		}
	}

	const dateCellRender = (date: Dayjs) => {
		const hasEntry = diaryEntries.some((e: StatusLogResponse) =>
			dayjs(e.logDate).isSame(date, 'day')
		)

		return (
			<div
				className={styles.calendarCell}
				onClick={() => handleDateSelect(date)}
			>
				{hasEntry ? (
					<BookOutlined className={styles.entryIcon} />
				) : (
					<Button
						type='dashed'
						size='small'
						className={styles.newEntryButton}
						icon={<PlusOutlined />}
					/>
				)}
			</div>
		)
	}

	const handleFileUpload = (file: File, type: 'photo' | 'document') => {
		setFilesToUpload(prev => ({
			...prev,
			[type + 's']: [...prev[type === 'photo' ? 'photos' : 'documents'], file],
		}))
		return false
	}

	return (
		<div className={styles.container}>
			<Button
				onClick={() => navigate(-1)}
				className={styles.backButton}
				type='primary'
			>
				← Назад к животному
			</Button>

			<Title level={2} className={styles.title}>
				Дневник наблюдений: {animal?.name}
			</Title>

			<div className={styles.layout}>
				<div className={styles.calendarSection}>
					<Calendar
						value={selectedDate}
						onChange={setSelectedDate}
						onSelect={handleDateSelect}
						dateCellRender={dateCellRender}
						className={styles.calendar}
					/>
				</div>

				<Card
					className={styles.entryCard}
					title={`Запись от ${selectedDate.format('DD.MM.YYYY')}`}
				>
					<Form form={form} layout='vertical' onFinish={handleSave}>
						<Form.Item label='Заметки' name='notes'>
							<Input.TextArea
								rows={3}
								placeholder='Опишите состояние животного...'
							/>
						</Form.Item>

						<div className={styles.parametersGrid}>
							<Form.Item label='Изменение веса (кг)' name='massChange'>
								<InputNumber
									step={0.1}
									addonBefore={
										<Tooltip title='Текущий вес'>{animal?.mass ?? '—'}</Tooltip>
									}
									className={styles.parameterInput}
								/>
							</Form.Item>

							<Form.Item label='Изменение роста (см)' name='heightChange'>
								<InputNumber
									step={0.1}
									addonBefore={
										<Tooltip title='Текущий рост'>
											{animal?.height ?? '—'}
										</Tooltip>
									}
									className={styles.parameterInput}
								/>
							</Form.Item>

							<Form.Item
								label='Изменение температуры (°C)'
								name='temperatureChange'
							>
								<InputNumber
									step={0.1}
									addonBefore={
										<Tooltip title='Текущая температура'>
											{animal?.temperature ?? '—'}
										</Tooltip>
									}
									className={styles.parameterInput}
								/>
							</Form.Item>

							<Form.Item label='Уровень активности' name='activityLevelChange'>
								<InputNumber
									min={1}
									max={10}
									addonBefore={
										<Tooltip title='Текущий уровень'>
											{animal?.activityLevel ?? '—'}
										</Tooltip>
									}
									className={styles.parameterInput}
								/>
							</Form.Item>

							<Form.Item label='Уровень аппетита' name='appetiteLevelChange'>
								<InputNumber
									min={1}
									max={10}
									addonBefore={
										<Tooltip title='Текущий уровень'>
											{animal?.appetiteLevel ?? '—'}
										</Tooltip>
									}
									className={styles.parameterInput}
								/>
							</Form.Item>
						</div>

						<div className={styles.actionsSection}>
							<FileUpload
								beforeUpload={file => handleFileUpload(file, 'photo')}
								accept='image/*'
								buttonText='Добавить фото'
								showUploadList={false}
								buttonIcon={undefined}
							/>

							<FileUpload
								beforeUpload={file => handleFileUpload(file, 'document')}
								accept='.pdf,.doc,.docx'
								buttonText='Добавить документ'
								showUploadList={false}
								buttonIcon={undefined}
							/>

							<Button
								type='primary'
								htmlType='submit'
								loading={createMutation.isPending || updateMutation.isPending}
								className={styles.saveButton}
							>
								{currentEntry ? 'Обновить запись' : 'Создать запись'}
							</Button>
						</div>

						{currentEntry && currentEntry?.photos?.length > 0 && (
							<div className={styles.photosSection}>
								<h3>Прикрепленные фото</h3>
								<div className={styles.photosGrid}>
									{currentEntry.photos.map((photo: string, index: number) => (
										<div key={index} className={styles.photoItem}>
											<Image
												src={photo}
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
						)}

						{currentEntry?.parameterChanges && (
							<ParameterHistory changes={currentEntry.parameterChanges} />
						)}
					</Form>
				</Card>
			</div>

			<Modal
				open={isPhotoModalOpen}
				footer={null}
				onCancel={() => setIsPhotoModalOpen(false)}
				width='80vw'
				destroyOnClose
			>
				{currentEntry?.photos?.[selectedPhotoIndex] && (
					<Image
						src={currentEntry.photos[selectedPhotoIndex]}
						style={{ maxHeight: '80vh', objectFit: 'contain' }}
					/>
				)}
			</Modal>
		</div>
	)
}
