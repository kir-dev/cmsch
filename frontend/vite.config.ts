import legacy from '@vitejs/plugin-legacy'
import react from '@vitejs/plugin-react-swc'
import { defineConfig } from 'vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), legacy({ modernPolyfills: true })],
  build: {
    rollupOptions: {
      output: {
        inlineDynamicImports: true
      }
    }
  },
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
