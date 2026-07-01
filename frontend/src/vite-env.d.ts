/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_DISABLE_APP_CONFIG_CACHE: string
  readonly VITE_APP_CONFIG_CACHE_TTL_SECONDS: string
  readonly VITE_HIDE_KIR_DEV_IN_FOOTER: string
  readonly VITE_CLIENT_BASE_URL: string
  readonly VITE_OFFICIAL_LANGUAGE: string
  readonly VITE_PASS_SERVER_URL: string
  readonly VITE_PASS_TEMPLATE: string
  readonly VITE_NEW_RIDDLE_ENDPOINTS: string
  readonly VITE_FIREBASE_PROJECT_ID: string
  readonly VITE_FIREBASE_APP_ID: string
  readonly VITE_FIREBASE_API_KEY: string
  readonly VITE_FIREBASE_SENDER_ID: string
  readonly VITE_FIREBASE_WEB_PUSH_PUBLIC_KEY: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

function processAndReportError(error)
