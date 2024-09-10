import eslintPlugin from '@typescript-eslint/eslint-plugin'
import tsParser from '@typescript-eslint/parser'
import reactHooksPlugin from 'eslint-plugin-react-hooks'
import prettierConfig from 'eslint-config-prettier'

export default [
  {
    files: ['*.ts', '*.tsx'],
    plugins: {
      '@typescript-eslint': eslintPlugin,
      'react-hooks': reactHooksPlugin
    },
    languageOptions: {
      parser: tsParser,
      sourceType: 'module'
    },
    rules: {
      ...prettierConfig.rules,
      'react/jsx-props-no-spreading': 'off',
      'react/require-default-props': 'off',
      'max-len': ['error', { code: 140, ignoreUrls: true }],
      'import/prefer-default-export': 'off',
      '@typescript-eslint/explicit-function-return-type': 'off'
    }
  }
]
