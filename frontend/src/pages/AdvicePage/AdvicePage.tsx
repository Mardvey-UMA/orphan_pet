import MarkdownText from '@/components/MarkdownText/MarkdownText'
import {
	useBertAdvice,
	useDeepSeekAnalysis,
} from '@/features/advice/hooks/useAdvice'
import { useUserAnimals } from '@/features/animal/hooks/useUserAnimals'
import {
	Alert,
	Button,
	Card,
	Col,
	Empty,
	Form,
	Input,
	Row,
	Spin,
	Tabs,
	Typography,
} from 'antd'
import { useState } from 'react'
import styles from './AdvicePage.module.scss'

const { Title } = Typography

export default function AdvicePage() {
	const [selectedAnimalId, setSelectedAnimalId] = useState<number | null>(null)
	const { data: animals = [] } = useUserAnimals()

	const [bertForm] = Form.useForm()

	const {
		mutate: predictDanger,
		data: bertResult,
		isPending: isBertLoading,
		error: bertError,
	} = useBertAdvice()

	const {
		mutate: analyzeAnimal,
		data: analysisResult,
		isPending: isAnalysisLoading,
		error: analysisError,
	} = useDeepSeekAnalysis()

	const handleBertSubmit = (values: {
		animalClass: string
		symptoms: string
	}) => {
		predictDanger({
			animal_class: values.animalClass,
			description: values.symptoms,
		})
	}

	const handleAnalysisSubmit = () => {
		if (!selectedAnimalId) return
		analyzeAnimal({ animal_id: selectedAnimalId })
	}

	return (
		<div className={styles.container}>
			<Title level={2} className={styles.title}>
				Консультация по здоровью питомца
			</Title>

			<Row gutter={[24, 24]}>
				<Col span={8}>
					<Card title='Ваши животные' className={styles.sideCard}>
						{animals.map(animal => (
							<div
								key={animal.id}
								className={`${styles.animalCard} ${
									selectedAnimalId === animal.id ? styles.selected : ''
								}`}
								onClick={() => setSelectedAnimalId(animal.id)}
							>
								{animal.name}
							</div>
						))}
					</Card>
				</Col>

				<Col span={16}>
					{selectedAnimalId ? (
						<Card className={styles.mainCard}>
							<Tabs defaultActiveKey='symptoms' className={styles.tabs}>
								<Tabs.TabPane tab='Проверка симптомов' key='symptoms'>
									<Alert
										type='warning'
										message='Важная информация'
										description='Ответы сгенерированы ИИ и могут быть неточными. Всегда консультируйтесь с ветеринаром для получения профессиональной диагностики.'
										showIcon
										className={styles.warningAlert}
									/>

									<Spin spinning={isBertLoading}>
										<Form
											form={bertForm}
											onFinish={handleBertSubmit}
											layout='vertical'
											className={styles.form}
										>
											<Form.Item
												name='animalClass'
												label='Вид животного'
												rules={[
													{ required: true, message: 'Укажите вид животного' },
												]}
											>
												<Input placeholder='Например: кошка, собака, попугай' />
											</Form.Item>

											<Form.Item
												name='symptoms'
												label='Описание симптомов'
												rules={[
													{
														required: true,
														message: 'Опишите наблюдаемые симптомы',
													},
												]}
											>
												<Input.TextArea
													rows={5}
													placeholder='Пример: вялость, повышенная температура, отказ от еды в течение суток'
												/>
											</Form.Item>

											<Button
												type='primary'
												htmlType='submit'
												size='large'
												className={styles.actionButton}
											>
												Анализировать симптомы
											</Button>
										</Form>

										{bertResult && (
											<Alert
												type={bertResult.data.is_dangerous ? 'error' : 'info'}
												message={
													bertResult.data.is_dangerous
														? 'Обнаружены опасные симптомы!'
														: 'Критических симптомов не обнаружено'
												}
												description={
													bertResult.data.is_dangerous &&
													'Немедленно обратитесь к ветеринару! Состояние животного требует срочного осмотра.'
												}
												showIcon
												className={styles.resultAlert}
											/>
										)}
									</Spin>
								</Tabs.TabPane>

								<Tabs.TabPane tab='Полная консультация' key='consultation'>
									<Alert
										type='warning'
										message='Важная информация'
										description='Анализ основан на исторических данных и может содержать неточности. Регулярно посещайте ветеринара для профилактических осмотров.'
										showIcon
										className={styles.warningAlert}
									/>

									<Spin
										spinning={isAnalysisLoading}
										tip='Генерируем рекомендации...'
									>
										<div className={styles.consultationContent}>
											<Button
												type='primary'
												size='large'
												onClick={handleAnalysisSubmit}
												className={styles.actionButton}
												disabled={isAnalysisLoading}
											>
												Сгенерировать полный отчёт
											</Button>

											{analysisResult && (
												<div className={styles.consultationResult}>
													<MarkdownText content={analysisResult.data.advice} />
												</div>
											)}
										</div>
									</Spin>
								</Tabs.TabPane>
							</Tabs>
						</Card>
					) : (
						<Empty
							description='Выберите животное из списка слева для получения консультации'
							className={styles.emptyPrompt}
						/>
					)}
				</Col>
			</Row>

			{(bertError || analysisError) && (
				<Alert
					type='error'
					message='Ошибка'
					description={(bertError || analysisError)?.message}
					className={styles.errorAlert}
				/>
			)}
		</div>
	)
}
