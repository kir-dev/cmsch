export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'
export const DISABLE_APP_CONFIG_CACHE = import.meta.env.VITE_DISABLE_APP_CONFIG_CACHE === 'true'
export const APP_CONFIG_CACHE_TTL_SECONDS = Number(import.meta.env.VITE_APP_CONFIG_CACHE_TTL_SECONDS ?? 3600)
export const HIDE_KIR_DEV_IN_FOOTER = import.meta.env.VITE_HIDE_KIR_DEV_IN_FOOTER === 'true'
export const CLIENT_BASE_URL = import.meta.env.VITE_CLIENT_BASE_URL ?? 'http://localhost:3000'
export const OFFICIAL_LANGUAGE = import.meta.env.VITE_OFFICIAL_LANGUAGE === 'true'
export const PASS_SERVER_URL = import.meta.env.VITE_PASS_SERVER_URL
export const PASS_TEMPLATE = import.meta.env.VITE_PASS_TEMPLATE
export const NEW_RIDDLE_ENDPOINTS = import.meta.env.VITE_NEW_RIDDLE_ENDPOINTS === 'true'
export const FIREBASE_PROJECT_ID = import.meta.env.VITE_FIREBASE_PROJECT_ID
export const FIREBASE_APP_ID = import.meta.env.VITE_FIREBASE_APP_ID
export const FIREBASE_API_KEY = import.meta.env.VITE_FIREBASE_API_KEY
export const FIREBASE_SENDER_ID = import.meta.env.VITE_FIREBASE_SENDER_ID
export const FIREBASE_WEB_PUSH_PUBLIC_KEY = import.meta.env.VITE_FIREBASE_WEB_PUSH_PUBLIC_KEY
