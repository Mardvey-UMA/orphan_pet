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
			navigate('/auth/login')
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
					name='token'
					rules={[
						{ required: true, message: 'Введите код из письма' },
						{ len: 6, message: 'Код должен содержать 6 символов' },
					]}
					className={styles.otpFormItem}
				>
					<div className={styles.otpContainer}>
						<Input.OTP
							length={6}
							type='numeric'
							formatter={str => str.toUpperCase()}
							className={styles.otpInput}
							autoFocus
						/>
					</div>
				</Form.Item>

				<Form.Item>
					<Button
						type='primary'
						htmlType='submit'
						loading={isActivating}
						block
						size='large'
					>
						Активировать
					</Button>
				</Form.Item>
			</Form>
		</div>
	)
}
