import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { AppRouter } from './router/AppRouter'
import './styles/base.module.scss'

const queryClient = new QueryClient({
	defaultOptions: {
		queries: {
			refetchOnWindowFocus: false,
			retry: 1,
		},
	},
})

export function App() {
	return (
		<QueryClientProvider client={queryClient}>
			<AppRouter />
			<ReactQueryDevtools initialIsOpen={false} />
		</QueryClientProvider>
	)
}
