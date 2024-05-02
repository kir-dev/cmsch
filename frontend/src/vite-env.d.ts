/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_BUGREPORT_URL: string
  readonly VITE_CLIENT_BASE_URL: string
  readonly VITE_OFFICIAL_LANGUAGE: string
  readonly VITE_PASS_SERVER_URL: string
  readonly VITE_PASS_TEMPLATE: string
  readonly VITE_NEW_RIDDLE_ENDPOINTS: string
  readonly VITE_INITIAL_BG_IMAGE: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
