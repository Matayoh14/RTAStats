import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, process.cwd(), "");
    return {
        plugins: [react()],
        server: {
            port: 5173,
            strictPort: true, // fail instead of silently switching to 5174
            proxy: {
                "/api": env.BACKEND_URL ?? "http://localhost:8080",
            },
        },
    };
});
