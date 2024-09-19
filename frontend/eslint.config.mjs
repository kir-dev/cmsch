import reactHooks from 'eslint-plugin-react-hooks'
import { fixupPluginRules } from '@eslint/compat'
import tsParser from '@typescript-eslint/parser'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import js from '@eslint/js'
import { FlatCompat } from '@eslint/eslintrc'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const compat = new FlatCompat({
  baseDirectory: __dirname,
  recommendedConfig: js.configs.recommended,
  allConfig: js.configs.all
})

export default [
  {
    ignores: ['**/node_modules', '**/dist', '.yarn/**/*']
  },
  ...compat.extends('prettier', 'plugin:prettier/recommended'),
  {
    plugins: {
      'react-hooks': fixupPluginRules(reactHooks)
    },

    languageOptions: {
      parser: tsParser,
      ecmaVersion: 2018,
      sourceType: 'module',

      parserOptions: {
        ecmaFeatures: {
          jsx: true
        }
      }
    },

    settings: {
      react: {
        version: 'detect'
      }
    },

    rules: {
      'react/jsx-props-no-spreading': 'off',
      'react/require-default-props': 'off',

      'max-len': [
        'error',
        {
          code: 140,
          ignoreUrls: true
        }
      ],

      'import/prefer-default-export': 'off',
      '@typescript-eslint/explicit-function-return-type': 'off'
    }
  }
]
