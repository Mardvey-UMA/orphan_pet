import { useAuth } from '@/features/auth/hooks/useAuth'
import { Button, Form, Input, message } from 'antd'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'
import styles from './LoginPage.module.scss'

interface LoginFormValues {
	identifier: string
	password: string
}

export default function LoginPage() {
	const [form] = Form.useForm<LoginFormValues>()
	const { login, isLoggingIn } = useAuth()
	const navigate = useNavigate()

	async function onFinish(values: LoginFormValues) {
		try {
			await login({
				indentifier: values.identifier,
				password: values.password,
			})
			message.success('Авторизация прошла успешно!')
			navigate('/profile')
		} catch (error: unknown) {
			if (axios.isAxiosError(error)) {
				const serverMsg = error.response?.data?.error || error.message
				message.error(`Ошибка авторизации: ${serverMsg}`)
			} else {
				message.error('Неизвестная ошибка при авторизации.')
			}
		}
	}

	return (
		<div className={styles.loginContainer}>
			<h1>Авторизация</h1>
			<Form
				form={form}
				layout='vertical'
				onFinish={onFinish}
				className={styles.loginForm}
			>
				<Form.Item
					label='Email или Username'
					name='identifier'
					rules={[{ required: true, message: 'Введите идентификатор' }]}
				>
					<Input placeholder='Введите email или username' />
				</Form.Item>

				<Form.Item
					label='Пароль'
					name='password'
					rules={[{ required: true, message: 'Введите пароль' }]}
				>
					<Input.Password placeholder='Введите пароль' />
				</Form.Item>

				<Form.Item>
					<Button
						type='primary'
						htmlType='submit'
						loading={isLoggingIn}
						block // Делает кнопку на всю ширину
					>
						Войти
					</Button>
				</Form.Item>
			</Form>
		</div>
	)
}
