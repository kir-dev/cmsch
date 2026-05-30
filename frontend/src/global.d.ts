interface WindowConfig {
  readonly API_BASE_URL: string | undefined
  readonly DISABLE_APP_CONFIG_CACHE: string | undefined
  readonly APP_CONFIG_CACHE_TTL_SECONDS: string | undefined
  readonly HIDE_KIR_DEV_IN_FOOTER: string | undefined
  readonly CLIENT_BASE_URL: string | undefined
  readonly OFFICIAL_LANGUAGE: string | undefined
  readonly PASS_SERVER_URL: string | undefined
  readonly PASS_TEMPLATE: string | undefined
  readonly FIREBASE_PROJECT_ID: string | undefined
  readonly FIREBASE_APP_ID: string | undefined
  readonly FIREBASE_API_KEY: string | undefined
  readonly FIREBASE_SENDER_ID: string | undefined
  readonly FIREBASE_WEB_PUSH_PUBLIC_KEY: string | undefined
  readonly PLAUSIBLE_URL: string | undefined
}

declare global {
  interface Window {
    config: WindowConfig
    processAndReportError: (error: unknown) => void
  }
}

export {}
