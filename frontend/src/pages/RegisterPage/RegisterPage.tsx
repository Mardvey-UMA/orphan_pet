import { useAuth } from '@/features/auth/hooks/useAuth'
import { Button, Form, Input, message } from 'antd'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'
import styles from './RegisterPage.module.scss'

interface RegisterFormValues {
	username: string
	email: string
	password: string
	confirmPassword: string
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
			navigate('/auth/activate')
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
					rules={[
						{ required: true, message: 'Введите пароль' },
						{ min: 6, message: 'Пароль должен быть не менее 6 символов' },
					]}
				>
					<Input.Password placeholder='Введите пароль' />
				</Form.Item>

				<Form.Item
					label='Подтвердите пароль'
					name='confirmPassword'
					dependencies={['password']}
					rules={[
						{ required: true, message: 'Подтвердите пароль' },
						({ getFieldValue }) => ({
							validator(_, value) {
								if (!value || getFieldValue('password') === value) {
									return Promise.resolve()
								}
								return Promise.reject(new Error('Пароли не совпадают!'))
							},
						}),
					]}
				>
					<Input.Password placeholder='Повторите пароль' />
				</Form.Item>

				<Form.Item>
					<Button
						type='primary'
						htmlType='submit'
						loading={isRegistering}
						block // Делает кнопку на всю ширину
					>
						Зарегистрироваться
					</Button>
				</Form.Item>
			</Form>
		</div>
	)
}
