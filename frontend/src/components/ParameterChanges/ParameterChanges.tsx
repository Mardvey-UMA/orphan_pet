import { ParameterChangeResponse } from '@/features/animal/api/types'
import { Card, Descriptions, Typography } from 'antd'

const { Text } = Typography

interface ParameterChangesProps {
	changes: ParameterChangeResponse[]
}

export const ParameterChanges = ({ changes }: ParameterChangesProps) => {
	if (!changes || changes.length === 0) {
		return <Text type='secondary'>Нет изменений параметров</Text>
	}

	return (
		<Card title='Изменения параметров' size='small'>
			<Descriptions bordered column={1} size='small'>
				{changes.map((change, index) => (
					<Descriptions.Item
						key={index}
						label={`${change.parameterName} (${change.changedAt})`}
					>
						{change.oldValue} → {change.newValue}
						<Text type='secondary' style={{ marginLeft: 8 }}>
							({change.changedBy})
						</Text>
					</Descriptions.Item>
				))}
			</Descriptions>
		</Card>
	)
}
