import { useAuth } from '@/features/auth/hooks/useAuth'
import {
	BookOutlined,
	CommentOutlined,
	HomeOutlined,
	LineChartOutlined,
	LogoutOutlined,
	MenuOutlined,
	UserOutlined,
} from '@ant-design/icons'
import { Button, Drawer, Layout, Menu } from 'antd'
import { useState } from 'react'
import { AiOutlineSafetyCertificate } from 'react-icons/ai'
import { Link, Outlet, useNavigate } from 'react-router-dom'
import styles from './MainLayout.module.scss'

const { Header, Content } = Layout

export function MainLayout() {
	const [isDrawerOpen, setIsDrawerOpen] = useState(false)
	const navigate = useNavigate()
	const { logout } = useAuth()
	function toggleDrawer() {
		setIsDrawerOpen(!isDrawerOpen)
	}

	const handleLogout = () => {
		logout()
		navigate('/auth/login')
	}

	const menuItems = [
		{
			key: 'home',
			icon: <HomeOutlined />,
			label: <Link to='/'>Главная</Link>,
		},
		{
			key: 'diary',
			icon: <BookOutlined />,
			label: <Link to='/diary'>Дневник</Link>,
		},
		{
			key: 'profile',
			icon: <UserOutlined />,
			label: <Link to='/profile'>Профиль</Link>,
		},
		{
			key: 'analytics',
			icon: <LineChartOutlined />,
			label: <Link to='/analytics'>Аналитика</Link>,
		},
		{
			key: 'advice',
			icon: <CommentOutlined />,
			label: <Link to='/advice'>Консультация ИИ</Link>,
		},
		{
			key: 'logout',
			icon: <LogoutOutlined />,
			label: 'Выход',
			onClick: handleLogout,
		},
	]

	return (
		<Layout className={styles.mainLayout}>
			<Header className={styles.topBar}>
				<div className={styles.headerContent}>
					<Button
						className={styles.burgerButton}
						icon={<MenuOutlined />}
						onClick={toggleDrawer}
						type='text'
					/>
					<div className={styles.logoContainer}>
						<AiOutlineSafetyCertificate className={styles.logoIcon} />
						<h2 className={styles.appName}>Pet Health Tracker</h2>
					</div>
				</div>
			</Header>

			<Drawer
				title={
					<div className={styles.drawerTitle}>
						<AiOutlineSafetyCertificate className={styles.drawerLogo} />
						<span>Меню</span>
					</div>
				}
				placement='left'
				closable
				onClose={toggleDrawer}
				open={isDrawerOpen}
			>
				<Menu mode='vertical' items={menuItems} className={styles.navMenu} />
			</Drawer>

			<Content className={styles.contentWrapper}>
				<Outlet />
			</Content>
		</Layout>
	)
}
