import { useUserAnimals } from '@/features/animal/hooks/useUserAnimals'
import { CalendarOutlined } from '@ant-design/icons'
import { Button, Card, Col, Row, Typography } from 'antd'
import dayjs from 'dayjs'
import { useNavigate } from 'react-router-dom'
import styles from './DiaryPage.module.scss'

const { Title, Text } = Typography

export default function DiaryPage() {
	const { data: animals = [], isPending } = useUserAnimals()
	const navigate = useNavigate()

	if (isPending) return <div>Загрузка животных...</div>

	return (
		<div className={styles.container}>
			<Title level={2}>Дневник состояния животных</Title>

			<Row gutter={[16, 16]} className={styles.animalsGrid}>
				{animals.map(animal => (
					<Col xs={24} sm={12} md={8} lg={6} key={animal.id}>
						<Card
							hoverable
							cover={
								animal.photos?.[0] ? (
									<img
										alt={animal.name}
										src={animal.photos[0]}
										className={styles.animalImage}
									/>
								) : (
									<div className={styles.noImage}>Нет фото</div>
								)
							}
							actions={[
								<Button
									type='link'
									icon={<CalendarOutlined />}
									onClick={() => navigate(`/animals/${animal.id}/diary`)}
								>
									Дневник
								</Button>,
							]}
						>
							<Card.Meta
								title={animal.name}
								description={
									<Text type='secondary'>
										{animal.birthDate
											? `${dayjs().diff(animal.birthDate, 'year')} лет`
											: 'Возраст неизвестен'}
									</Text>
								}
							/>
						</Card>
					</Col>
				))}
			</Row>
		</div>
	)
}
