import { useAuth } from '@/features/auth/hooks/useAuth'
import { Button, Form, Input, message } from 'antd'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'
import styles from './RegisterPage.module.scss'

interface RegisterFormValues {
	username: string
	email: string
	password: string
}

export default function RegisterPage() {
	const [form] = Form.useForm<RegisterFormValues>()
	const { register, isRegistering } = useAuth()
	const navigate = useNavigate()

	async function onFinish(values: RegisterFormValues) {
		try {
			await register({
				username: values.username,
				email: values.email,
				password: values.password,
			})
			message.success(
				'Регистрация прошла успешно! Проверьте почту для активации.'
			)
			navigate('/activate')
		} catch (error: unknown) {
			if (axios.isAxiosError(error)) {
				const serverMsg = error.response?.data?.error || error.message
				message.error(`Ошибка регистрации: ${serverMsg}`)
			} else {
				message.error('Неизвестная ошибка при регистрации.')
			}
		}
	}

	return (
		<div className={styles.registerContainer}>
			<h1>Регистрация</h1>
			<Form
				form={form}
				layout='vertical'
				onFinish={onFinish}
				className={styles.registerForm}
			>
				<Form.Item
					label='Имя пользователя'
					name='username'
					rules={[{ required: true, message: 'Введите имя пользователя' }]}
				>
					<Input placeholder='Введите имя пользователя' />
				</Form.Item>

				<Form.Item
					label='Email'
					name='email'
					rules={[
						{ required: true, message: 'Введите email' },
						{ type: 'email', message: 'Неверный формат email' },
					]}
				>
					<Input placeholder='Введите email' />
				</Form.Item>

				<Form.Item
					label='Пароль'
					name='password'
					rules={[{ required: true, message: 'Введите пароль' }]}
				>
					<Input.Password placeholder='Введите пароль' />
				</Form.Item>

				<Form.Item>
					<Button type='primary' htmlType='submit' loading={isRegistering}>
						Зарегистрироваться
					</Button>
				</Form.Item>
			</Form>
		</div>
	)
}
