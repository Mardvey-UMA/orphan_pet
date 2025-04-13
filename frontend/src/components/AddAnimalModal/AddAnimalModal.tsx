import { animalApi } from '@/features/animal/api/animalApi'
import { AnimalCreateRequest } from '@/features/animal/api/types'
import { useAnimal } from '@/features/animal/hooks/useAnimal'
import { PlusOutlined, UploadOutlined } from '@ant-design/icons'
import { Button, DatePicker, Form, Input, message, Modal, Select } from 'antd'
import { RcFile } from 'antd/es/upload'
import dayjs from 'dayjs'
import { useState } from 'react'
import { FileUpload } from '../FileUpload/FileUpload'
import styles from './AddAnimalModal.module.scss'

interface AddAnimalModalProps {
	open: boolean
	onClose: () => void
	onAnimalCreated: () => void
}

interface AddAnimalFormValues {
	name: string
	description?: string
	birthDate?: dayjs.Dayjs
	mass?: string
}

const documentTypes = [
	{ value: 'PASSPORT', label: 'Паспорт' },
	{ value: 'VACCINATION', label: 'Вакцинация' },
	{ value: 'MEDICAL', label: 'Медкарта' },
	{ value: 'OTHER', label: 'Другое' },
]

const acceptedFileTypes = {
	photo: ['image/jpeg', 'image/png', 'image/webp'],
	document: [
		'application/pdf',
		'application/msword',
		'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
	],
}

export const AddAnimalModal = ({
	open,
	onClose,
	onAnimalCreated,
}: AddAnimalModalProps) => {
	const [form] = Form.useForm<AddAnimalFormValues>()
	const [photoFile, setPhotoFile] = useState<RcFile | null>(null)
	const [documentFile, setDocumentFile] = useState<RcFile | null>(null)
	const [documentType, setDocumentType] = useState<string>('PASSPORT')
	const { createAnimal, isCreating } = useAnimal()

	const handleSubmit = async () => {
		try {
			const values = await form.validateFields()

			// 1. Создаем животное
			const animalData: AnimalCreateRequest = {
				name: values.name,
				description: values.description,
				birthDate: values.birthDate?.format('YYYY-MM-DD'),
				mass: values.mass ? Number(values.mass) : undefined,
				attributes: [],
			}

			const animal = await createAnimal(animalData)

			// 2. Загружаем файлы если есть
			const uploads = []

			if (photoFile) {
				uploads.push(
					animalApi.uploadAnimalPhoto(animal.id, photoFile).then(({ url }) => {
						message.success('Фото успешно загружено!')
						return url
					})
				)
			}

			if (documentFile) {
				uploads.push(
					animalApi
						.uploadAnimalDocument(animal.id, documentFile, documentType)
						.then(({ url }) => {
							message.success('Документ успешно загружен!')
							return url
						})
				)
			}

			await Promise.all(uploads)

			message.success('Животное успешно создано!')
			onAnimalCreated()
			onClose()
			form.resetFields()
			setPhotoFile(null)
			setDocumentFile(null)
		} catch (error) {
			console.error('Create animal error:', error)
			message.error('Ошибка при создании животного')
		}
	}

	const validateFile = (file: RcFile, type: 'photo' | 'document') => {
		const isValidType = acceptedFileTypes[type].includes(file.type)
		if (!isValidType) {
			message.error(
				`Неподдерживаемый формат файла. Разрешены: ${acceptedFileTypes[
					type
				].join(', ')}`
			)
			return false
		}

		const isLt5M = file.size / 1024 / 1024 < 5
		if (!isLt5M) {
			message.error('Файл должен быть меньше 5MB!')
			return false
		}

		return true
	}

	return (
		<Modal
			title='Добавить животное'
			open={open}
			onOk={handleSubmit}
			onCancel={onClose}
			confirmLoading={isCreating}
			width={800}
			okText='Создать'
			cancelText='Отмена'
		>
			<Form form={form} layout='vertical' className={styles.form}>
				<Form.Item
					name='name'
					label='Имя животного'
					rules={[{ required: true, message: 'Введите имя животного' }]}
				>
					<Input placeholder='Например, Барсик' />
				</Form.Item>

				<Form.Item name='description' label='Описание'>
					<Input.TextArea
						placeholder='Порода, особенности характера и т.д.'
						rows={3}
					/>
				</Form.Item>

				<Form.Item name='birthDate' label='Дата рождения'>
					<DatePicker style={{ width: '100%' }} placeholder='Выберите дату' />
				</Form.Item>

				<Form.Item name='mass' label='Вес (кг)'>
					<Input
						type='number'
						placeholder='Введите вес в килограммах'
						addonAfter='кг'
					/>
				</Form.Item>

				<div className={styles.fileSection}>
					<h4>Фото животного</h4>
					<FileUpload
						beforeUpload={(file: RcFile) => {
							if (!validateFile(file, 'photo')) return false
							setPhotoFile(file)
							return false
						}}
						accept='image/*'
						buttonText='Загрузить фото'
						buttonIcon={<UploadOutlined />}
					/>
					{photoFile && (
						<div className={styles.fileInfo}>
							<span>{photoFile.name}</span>
							<Button type='link' danger onClick={() => setPhotoFile(null)}>
								Удалить
							</Button>
						</div>
					)}
				</div>

				<div className={styles.fileSection}>
					<h4>Документы</h4>
					<Form.Item label='Тип документа'>
						<Select
							value={documentType}
							onChange={setDocumentType}
							options={documentTypes}
							className={styles.docSelect}
						/>
					</Form.Item>
					<FileUpload
						beforeUpload={(file: RcFile) => {
							if (!validateFile(file, 'document')) return false
							setDocumentFile(file)
							return false
						}}
						accept='.pdf,.doc,.docx'
						buttonText='Загрузить документ'
						buttonIcon={<PlusOutlined />}
					/>
					{documentFile && (
						<div className={styles.fileInfo}>
							<span>{documentFile.name}</span>
							<Button type='link' danger onClick={() => setDocumentFile(null)}>
								Удалить
							</Button>
						</div>
					)}
				</div>
			</Form>
		</Modal>
	)
}
