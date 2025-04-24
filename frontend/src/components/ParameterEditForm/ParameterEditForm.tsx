import { ParameterType } from '@/features/diary/api/types'
import { Button, Form, InputNumber, Space, Typography } from 'antd'

interface ParameterEditFormProps {
	initialValues?: Partial<Record<ParameterType, number>>
	onSave: (values: Record<ParameterType, number | undefined>) => void
}

export const ParameterEditForm = ({
	initialValues,
	onSave,
}: ParameterEditFormProps) => {
	const [form] = Form.useForm()

	return (
		<Form
			form={form}
			initialValues={initialValues}
			layout='vertical'
			onFinish={onSave}
		>
			<Typography.Title level={5}>Изменение параметров</Typography.Title>

			<Form.Item name='mass' label='Вес (кг)'>
				<InputNumber step={0.1} precision={1} />
			</Form.Item>

			<Form.Item name='height' label='Рост (см)'>
				<InputNumber step={0.1} precision={1} />
			</Form.Item>

			<Form.Item name='temperature' label='Температура (°C)'>
				<InputNumber step={0.1} precision={1} />
			</Form.Item>

			<Form.Item name='activityLevel' label='Уровень активности (1-10)'>
				<InputNumber min={1} max={10} />
			</Form.Item>

			<Form.Item name='appetiteLevel' label='Уровень аппетита (1-10)'>
				<InputNumber min={1} max={10} />
			</Form.Item>

			<Space>
				<Button type='primary' htmlType='submit'>
					Сохранить изменения
				</Button>
				<Button onClick={() => form.resetFields()}>Сбросить</Button>
			</Space>
		</Form>
	)
}
