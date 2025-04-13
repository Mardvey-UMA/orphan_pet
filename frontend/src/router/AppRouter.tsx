import { createBrowserRouter, RouterProvider } from 'react-router-dom'

// Импорты ваших Layout-компонентов
import { AuthLayout } from '@/layouts/AuthLayout/AuthLayout'
import { MainLayout } from '@/layouts/MainLayout/MainLayout'

// Импорты страниц
import ActivateAccountPage from '@/pages/ActivateAccountPage/ActivateAccountPage'
import AnimalPage from '@/pages/AnimalPage/AnimalPage'
import DiaryPage from '@/pages/DiaryPage/DiaryPage'
import HomePage from '@/pages/HomePage/HomePage'
import LoginPage from '@/pages/LoginPage/LoginPage'
import NotFoundPage from '@/pages/NotFoundPage/NotFoundPage'
import ProfilePage from '@/pages/ProfilePage/ProfilePage'
import RegisterPage from '@/pages/RegisterPage/RegisterPage'

// Создаем роутер с двумя родительскими маршрутами:
// 1) "/auth" (AuthLayout) - логин, регистрация, активация
// 2) "/" (MainLayout) - остальные страницы приложения
const router = createBrowserRouter([
	{
		path: '/auth',
		element: <AuthLayout />,
		children: [
			{ path: 'login', element: <LoginPage /> },
			{ path: 'register', element: <RegisterPage /> },
			{ path: 'activate', element: <ActivateAccountPage /> },
			// Если хотим ловить несуществующие роуты внутри /auth
			{ path: '*', element: <NotFoundPage /> },
		],
	},
	{
		path: '/',
		element: <MainLayout />,
		children: [
			// Главная (при заходе на "/")
			{ index: true, element: <HomePage /> },
			// Прочие страницы
			{ path: 'animals/:id', element: <AnimalPage /> },
			{ path: 'diary', element: <DiaryPage /> },
			{ path: 'profile', element: <ProfilePage /> },
			// Ловим все несуществующие пути
			{ path: '*', element: <NotFoundPage /> },
		],
	},
])

export function AppRouter() {
	return <RouterProvider router={router} />
}
