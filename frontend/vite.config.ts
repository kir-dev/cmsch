import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    open: 'http://127.0.0.1:3000',
    port: 3000
  },
  preview: {
    host: '0.0.0.0',
    open: true,
    port: 3000
  }
})
