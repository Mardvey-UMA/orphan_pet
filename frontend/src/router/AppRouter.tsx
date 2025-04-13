import { createBrowserRouter, RouterProvider } from 'react-router-dom'

import { AuthLayout } from '@/layouts/AuthLayout/AuthLayout'
import { MainLayout } from '@/layouts/MainLayout/MainLayout'

import ActivateAccountPage from '@/pages/ActivateAccountPage/ActivateAccountPage'
import { AnimalDiaryPage } from '@/pages/AnimalDiaryPage/AnimalDiaryPage'
import { AnimalPage } from '@/pages/AnimalPage/AnimalPage'
import DiaryPage from '@/pages/DiaryPage/DiaryPage'
import HomePage from '@/pages/HomePage/HomePage'
import LoginPage from '@/pages/LoginPage/LoginPage'
import NotFoundPage from '@/pages/NotFoundPage/NotFoundPage'
import ProfilePage from '@/pages/ProfilePage/ProfilePage'
import RegisterPage from '@/pages/RegisterPage/RegisterPage'

const router = createBrowserRouter([
	{
		path: '/auth',
		element: <AuthLayout />,
		children: [
			{ path: 'login', element: <LoginPage /> },
			{ path: 'register', element: <RegisterPage /> },
			{ path: 'activate', element: <ActivateAccountPage /> },
			{ path: '*', element: <NotFoundPage /> },
		],
	},
	{
		path: '/',
		element: <MainLayout />,
		children: [
			{ index: true, element: <HomePage /> },
			{
				path: 'animals/:id/diary',
				element: <AnimalDiaryPage />,
			},
			{ path: 'animals/:id', element: <AnimalPage /> },
			{ path: 'diary', element: <DiaryPage /> },
			{ path: 'profile', element: <ProfilePage /> },
			{ path: '*', element: <NotFoundPage /> },
		],
	},
])

export function AppRouter() {
	return <RouterProvider router={router} />
}
