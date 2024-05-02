import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    open: true,
    port: 3000
  },
  preview: {
    host: '0.0.0.0',
    open: true,
    port: 3000
  }
})
