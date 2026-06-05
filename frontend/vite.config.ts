import babel from '@rolldown/plugin-babel'
import legacy from '@vitejs/plugin-legacy'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import * as path from 'path'
import { defineConfig } from 'vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), babel({ presets: [reactCompilerPreset()] }), legacy({ modernPolyfills: true })],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  build: {
    rolldownOptions: {
      optimization: { inlineConst: true, pifeForModuleWrappers: true },
      output: { codeSplitting: false }
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
