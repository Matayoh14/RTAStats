import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import RTAStatsApp from './components/RTAStatsApp.jsx'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'

const queryClient = new QueryClient()

createRoot(document.getElementById('root')).render(
  <StrictMode >
    <QueryClientProvider client={queryClient}>
      <RTAStatsApp />
    </QueryClientProvider>
  </StrictMode >
)
