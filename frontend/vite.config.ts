import legacy from '@vitejs/plugin-legacy'
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react({
      babel: {
        plugins: [['babel-plugin-react-compiler']]
      }
    }),
    legacy({ modernPolyfills: true })
  ],
  build: {
    rolldownOptions: {
      optimization: { inlineConst: true, pifeForModuleWrappers: true },
      output: { inlineDynamicImports: true }
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
