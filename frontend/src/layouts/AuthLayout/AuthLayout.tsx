import { Layout } from 'antd'
import { AiOutlineSafetyCertificate } from 'react-icons/ai'
import { Outlet } from 'react-router-dom'
import styles from './AuthLayout.module.scss'

// Можно также использовать "Layout.Header" из antd.
export function AuthLayout() {
	return (
		<Layout className={styles.authLayout}>
			<header className={styles.topBar}>
				{/* Лого или простая иконка */}
				<AiOutlineSafetyCertificate className={styles.logoIcon} />
				<h2 className={styles.appName}>Pet Health Tracker</h2>
			</header>

			{/* Основное содержимое (страница /auth/login, /auth/register, /auth/activate) */}
			<div className={styles.contentWrapper}>
				<Outlet />
			</div>
		</Layout>
	)
}
