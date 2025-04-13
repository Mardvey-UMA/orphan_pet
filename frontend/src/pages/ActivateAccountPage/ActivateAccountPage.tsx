import { useAuth } from '@/features/auth/hooks/useAuth'
import { Button, Form, Input, message } from 'antd'
import { useNavigate } from 'react-router-dom'
import styles from './ActivateAccountPage.module.scss'

export default function ActivateAccountPage() {
	const [form] = Form.useForm()
	const { activate, isActivating } = useAuth()
	const navigate = useNavigate()

	const onFinish = async (values: { token: string }) => {
		try {
			await activate(values.token)
			message.success('Аккаунт успешно активирован!')
			navigate('/login')
		} catch (error) {
			message.error(`Ошибка активации: ${error ?? 'Неизвестная'}`)
		}
	}

	return (
		<div className={styles.activateContainer}>
			<h1>Активация аккаунта</h1>
			<p>Пожалуйста, введите код активации, высланный на вашу почту.</p>

			<Form
				form={form}
				layout='vertical'
				onFinish={onFinish}
				className={styles.activateForm}
			>
				<Form.Item
					label='Код активации (token)'
					name='token'
					rules={[{ required: true, message: 'Введите код из почты' }]}
				>
					<Input />
				</Form.Item>

				<Form.Item>
					<Button type='primary' htmlType='submit' loading={isActivating}>
						Активировать
					</Button>
				</Form.Item>
			</Form>
		</div>
	)
}
