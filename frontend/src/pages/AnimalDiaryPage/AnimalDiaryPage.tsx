// pages/AnimalDiaryPage/AnimalDiaryPage.tsx
import { FileUpload } from '@/components/FileUpload/FileUpload'
import { ParameterHistory } from '@/components/ParametrHistory/ParameterHistory'
import { diaryApi } from '@/features/diary/api/diaryApi'
import { StatusLogCreateRequest } from '@/features/diary/api/types'
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

const { Title } = Typography

export const AnimalDiaryPage = () => {
	const { id } = useParams<{ id: string }>()
	const navigate = useNavigate()
	const queryClient = useQueryClient()
	const [form] = Form.useForm()
	const [selectedDate, setSelectedDate] = useState<Dayjs>(dayjs())
	const [isPhotoModalOpen, setIsPhotoModalOpen] = useState(false)
	const [selectedPhotoIndex, setSelectedPhotoIndex] = useState(0)

	// Получение записей дневника
	const { data: diaryEntries } = useQuery({
		queryKey: ['diaryEntries', id],
		queryFn: () => diaryApi.getStatusLogs(Number(id!)),
	})

	// Мутации
	const createMutation = useMutation({
		mutationFn: (data: StatusLogCreateRequest) =>
			diaryApi.createStatusLog(Number(id!), data),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['diaryEntries', id] })
		},
	})

	const updateMutation = useMutation({
		mutationFn: (data: StatusLogCreateRequest) => {
			const entry = diaryEntries?.data?.find(e =>
				dayjs(e.logDate).isSame(selectedDate, 'day')
			)
			if (!entry) throw new Error('Запись не найдена')
			return diaryApi.updateStatusLog(Number(id!), entry.id, data)
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['diaryEntries', id] })
		},
	})

	const addPhotoMutation = useMutation({
		mutationFn: async (file: File) => {
			const entry = diaryEntries?.data?.find(e =>
				dayjs(e.logDate).isSame(selectedDate, 'day')
			)
			if (!entry) throw new Error('Запись не найдена')
			return diaryApi.addStatusLogPhoto(Number(id!), entry.id, file)
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['diaryEntries', id] })
		},
	})

	const currentEntry = diaryEntries?.data?.find(e =>
		dayjs(e.logDate).isSame(selectedDate, 'day')
	)
	const addDocumentMutation = useMutation({
		mutationFn: async (file: File) => {
			const entry = diaryEntries?.data?.find(e =>
				dayjs(e.logDate).isSame(selectedDate, 'day')
			)

			if (!entry?.id) {
				throw new Error('Не найдена запись для добавления документа')
			}

			return diaryApi.addStatusLogDocument(
				Number(id!),
				entry.id,
				file,
				'MEDICAL'
			)
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['diaryEntries', id] })
		},
	})
	const handleSave = async (values: StatusLogCreateRequest) => {
		try {
			const data = {
				...values,
				logDate: selectedDate.format('YYYY-MM-DD'),
			}

			if (
				diaryEntries?.data?.some(e =>
					dayjs(e.logDate).isSame(selectedDate, 'day')
				)
			) {
				await updateMutation.mutateAsync(data)
			} else {
				await createMutation.mutateAsync(data)
			}
			message.success('Запись сохранена')
		} catch (error) {
			message.error('Ошибка сохранения')
			console.log(error)
		}
	}

	const dateCellRender = (date: Dayjs) => {
		const hasEntry = diaryEntries?.data?.some(e =>
			dayjs(e.logDate).isSame(date, 'day')
		)
		return hasEntry ? <div className={styles.calendarDot} /> : null
	}

	return (
		<div className={styles.container}>
			<Button onClick={() => navigate(-1)} className={styles.backButton}>
				Назад к животному
			</Button>

			<Title level={2} className={styles.title}>
				Дневник наблюдений
			</Title>

			<div className={styles.layout}>
				<div className={styles.calendarSection}>
					<Calendar
						value={selectedDate}
						onChange={setSelectedDate}
						dateCellRender={dateCellRender}
						className={styles.calendar}
					/>
				</div>

				<Card className={styles.entryCard}>
					<Form
						form={form}
						initialValues={currentEntry}
						onFinish={handleSave}
						layout='vertical'
					>
						<Form.Item label='Дата записи'>
							<Input disabled value={selectedDate.format('DD.MM.YYYY')} />
						</Form.Item>

						<Form.Item label='Заметки' name='notes'>
							<Input.TextArea
								rows={4}
								placeholder='Введите ваши наблюдения...'
							/>
						</Form.Item>

						<div className={styles.parametersGrid}>
							<Form.Item label='Изменение веса (кг)' name='massChange'>
								<Input type='number' step='0.1' />
							</Form.Item>

							<Form.Item label='Изменение роста (см)' name='heightChange'>
								<Input type='number' step='0.1' />
							</Form.Item>

							<Form.Item
								label='Изменение температуры (°C)'
								name='temperatureChange'
							>
								<Input type='number' step='0.1' />
							</Form.Item>

							<Form.Item
								label='Уровень активности (1-10)'
								name='activityLevelChange'
							>
								<Input type='number' min={1} max={10} />
							</Form.Item>

							<Form.Item
								label='Уровень аппетита (1-10)'
								name='appetiteLevelChange'
							>
								<Input type='number' min={1} max={10} />
							</Form.Item>
						</div>

						{currentEntry && currentEntry?.photos?.length > 0 && (
							<div className={styles.photosSection}>
								<h3>Прикреплённые фото</h3>
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

						<div className={styles.uploadSection}>
							<FileUpload
								beforeUpload={(file: File) => {
									addPhotoMutation.mutate(file)
									return false
								}}
								accept='image/*'
								buttonText='Добавить фото'
								buttonIcon={undefined}
							/>
						</div>
						<div className={styles.uploadSection}>
							<FileUpload
								beforeUpload={(file: File) => {
									addDocumentMutation.mutate(file)
									return false
								}}
								accept='.pdf,.doc,.docx'
								buttonText='Добавить документ'
								buttonIcon={undefined}
							/>
						</div>
						{currentEntry?.parameterChanges && (
							<ParameterHistory changes={currentEntry.parameterChanges} />
						)}

						<Button
							type='primary'
							htmlType='submit'
							loading={createMutation.isPending || updateMutation.isPending}
							className={styles.saveButton}
						>
							Сохранить изменения
						</Button>
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
