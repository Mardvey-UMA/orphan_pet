import { MenuOutlined } from '@ant-design/icons'
import { Button, Drawer, Layout } from 'antd'
import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import styles from './MainLayout.module.scss'

// Можно взять Layout.Header/Layout.Content/Layout.Footer из antd
const { Header, Content } = Layout

export function MainLayout() {
	const [isDrawerOpen, setIsDrawerOpen] = useState(false)

	function toggleDrawer() {
		setIsDrawerOpen(!isDrawerOpen)
	}

	return (
		<Layout className={styles.mainLayout}>
			<Header className={styles.topBar}>
				<Button
					className={styles.burgerButton}
					icon={<MenuOutlined />}
					onClick={toggleDrawer}
					type='text'
				/>
				<h2 className={styles.appName}>Pet Health Tracker</h2>
			</Header>

			<Drawer
				title='Меню'
				placement='left'
				closable
				onClose={toggleDrawer}
				open={isDrawerOpen}
			>
				{/* Тут ваши пункты меню (ссылки), например */}
				<p>Пункт 1</p>
				<p>Пункт 2</p>
			</Drawer>

			<Content className={styles.contentWrapper}>
				<Outlet />
			</Content>
		</Layout>
	)
}
