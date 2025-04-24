import { ParameterStats } from '@/features/diary/api/types'
import { Card, Descriptions } from 'antd'

interface ParameterStatsProps {
	stats: Record<string, ParameterStats>
}

export const ParameterStatsDisplay = ({ stats }: ParameterStatsProps) => {
	const parameters = [
		{ key: 'mass', name: 'Вес (кг)', precision: 1 },
		{ key: 'height', name: 'Рост (см)', precision: 1 },
		{ key: 'temperature', name: 'Температура (°C)', precision: 1 },
		{ key: 'activityLevel', name: 'Активность (1-10)', precision: 0 },
		{ key: 'appetiteLevel', name: 'Аппетит (1-10)', precision: 0 },
	]

	return (
		<Card title='Статистика параметров'>
			<Descriptions bordered column={1}>
				{parameters.map(param => {
					const stat = stats[param.key]
					if (!stat) return null

					return (
						<Descriptions.Item key={param.key} label={param.name}>
							{stat.minValue?.toFixed(param.precision)} —{' '}
							{stat.maxValue?.toFixed(param.precision)}
							{stat.avgValue &&
								` (Среднее: ${stat.avgValue.toFixed(param.precision)})`}
						</Descriptions.Item>
					)
				})}
			</Descriptions>
		</Card>
	)
}
