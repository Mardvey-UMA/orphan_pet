import { analyticsApi } from '@/features/animal/api/analyticsApi'
import { useUserAnimals } from '@/features/animal/hooks/useUserAnimals'
import { useQuery } from '@tanstack/react-query'
import {
	Card,
	Col,
	Descriptions,
	Empty,
	Row,
	Spin,
	Tabs,
	Typography,
} from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import {
	CartesianGrid,
	Legend,
	Line,
	LineChart,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis,
} from 'recharts'
import styles from './AnalyticsPage.module.scss'

const { Title } = Typography

export const AnalyticsPage = () => {
	const { animalId } = useParams<{ animalId: string }>()
	const { data: animals = [] } = useUserAnimals()
	const navigate = useNavigate()

	const { data: analytics, isPending } = useQuery({
		queryKey: ['animalAnalytics', animalId],
		queryFn: () => analyticsApi.getAnimalAnalytics(Number(animalId!)),
		enabled: !!animalId,
	})

	const renderParameterChart = (parameter: string) => {
		const parameterData = analytics?.find(a => a.parameterName === parameter)
		if (!parameterData) return <Empty />

		const chartData = parameterData.changes.map(d => {
			let numericValue = parseFloat(d.newValue)

			if (isNaN(numericValue) || numericValue < 0) {
				numericValue = 0
			}

			return {
				date: new Date(d.changedAt).toLocaleDateString(),
				value: numericValue,
				type: parameter,
			}
		})

		return (
			<ResponsiveContainer width='100%' height={400}>
				<LineChart
					data={chartData}
					margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
				>
					<CartesianGrid strokeDasharray='3 3' />
					<XAxis dataKey='date' />
					<YAxis
						label={{
							value: parameterNames[parameter] || parameter,
							angle: -90,
							position: 'insideLeft',
						}}
					/>
					<Tooltip
						formatter={value => [value, parameterNames[parameter] || parameter]}
						labelFormatter={date => `Дата: ${date}`}
					/>
					<Legend />
					<Line
						type='monotone'
						dataKey='value'
						name={parameterNames[parameter] || parameter}
						stroke='#1890ff'
						strokeWidth={2}
						dot={{ r: 4 }}
						activeDot={{ r: 6 }}
					/>
				</LineChart>
			</ResponsiveContainer>
		)
	}

	const renderStatsCard = (parameter: string) => {
		const stats = analytics?.find(a => a.parameterName === parameter)?.stats
		if (!stats) return <Empty />

		const allowNegativeValues = ['nothing']

		const formatStatValue = (value: string | number) => {
			const numValue = typeof value === 'string' ? parseFloat(value) : value

			if (isNaN(numValue)) return 'Н/Д'

			if (!allowNegativeValues.includes(parameter) && numValue < 0) {
				return '0'
			}

			switch (parameter) {
				case 'mass':
				case 'height':
				case 'temperature':
					return numValue.toFixed(2)
				case 'activityLevel':
				case 'appetiteLevel':
					return numValue.toFixed(0)
				default:
					return numValue.toString()
			}
		}

		return (
			<Card>
				<Descriptions bordered column={1}>
					<Descriptions.Item label='Минимальное'>
						{formatStatValue(stats.minValue)}
					</Descriptions.Item>
					<Descriptions.Item label='Максимальное'>
						{formatStatValue(stats.maxValue)}
					</Descriptions.Item>
					<Descriptions.Item label='Среднее'>
						{formatStatValue(stats.avgValue)}
					</Descriptions.Item>
				</Descriptions>
			</Card>
		)
	}

	return (
		<div className={styles.container}>
			<Title level={2}>Аналитика состояния животных</Title>

			<Row gutter={[24, 24]}>
				<Col span={8}>
					<Card title='Ваши животные'>
						{animals.map(animal => (
							<div
								key={animal.id}
								className={styles.animalCard}
								onClick={() => navigate(`/analytics/${animal.id}`)}
							>
								{animal.name}
							</div>
						))}
					</Card>
				</Col>

				<Col span={16}>
					{animalId ? (
						<Spin spinning={isPending}>
							<Tabs
								items={parametersList.map(param => ({
									key: param.key,
									label: param.label,
									children: (
										<Row gutter={[24, 24]}>
											<Col span={16}>{renderParameterChart(param.key)}</Col>
											<Col span={8}>{renderStatsCard(param.key)}</Col>
										</Row>
									),
								}))}
							/>
						</Spin>
					) : (
						<Empty description='Выберите животное для просмотра аналитики' />
					)}
				</Col>
			</Row>
		</div>
	)
}

const parametersList = [
	{ key: 'mass', label: 'Вес' },
	{ key: 'height', label: 'Рост' },
	{ key: 'temperature', label: 'Температура' },
	{ key: 'activityLevel', label: 'Активность' },
	{ key: 'appetiteLevel', label: 'Аппетит' },
]

const parameterNames: Record<string, string> = {
	mass: 'Вес (кг)',
	height: 'Рост (см)',
	temperature: 'Температура (°C)',
	activityLevel: 'Уровень активности',
	appetiteLevel: 'Уровень аппетита',
}
