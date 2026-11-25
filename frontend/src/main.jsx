import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import RTAStatsApp from './components/RTAStatsApp.jsx'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { AuthProvider } from './components/AuthContext.jsx'

const queryClient = new QueryClient()

createRoot(document.getElementById('root')).render(
  <StrictMode >
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <RTAStatsApp />
      </AuthProvider>
    </QueryClientProvider>
  </StrictMode >
)
