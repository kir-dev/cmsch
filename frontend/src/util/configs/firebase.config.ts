import {
  FIREBASE_API_KEY,
  FIREBASE_APP_ID,
  FIREBASE_PROJECT_ID,
  FIREBASE_SENDER_ID,
  FIREBASE_WEB_PUSH_PUBLIC_KEY
} from './environment.config.ts'
import { initializeApp } from '@firebase/app'
import { getMessaging, getToken, MessagePayload, Messaging, onMessage } from '@firebase/messaging'
import axios from 'axios'
import { ApiPaths } from '../paths.ts'

function getFirebaseConfig() {
  return {
    apiKey: FIREBASE_API_KEY,
    appId: FIREBASE_APP_ID,
    projectId: FIREBASE_PROJECT_ID,
    messagingSenderId: FIREBASE_SENDER_ID
  }
}

export function getCloudMessaging(): Messaging | null {
  try {
    const firebaseApp = initializeApp(getFirebaseConfig())
    return getMessaging(firebaseApp)
  } catch (e) {
    console.error('Failed to initialize Firebase Cloud Messaging', e)
    return null
  }
}

export async function initNotifications(messaging: Messaging, onNotification: (payload: MessagePayload) => void): Promise<void> {
  onMessage(messaging, onNotification)
  const token = await getMessagingToken(messaging)
  if (!token) return

  await axios.post(ApiPaths.ADD_PUSH_NOTIFICATION_TOKEN, { token })
}

export async function disableNotifications() {
  return unregisterServiceWorker()
}

export async function unsubscribeFromNotifications() {
  if (Notification.permission != 'granted') return // There is nothing to unsubscribe from

  try {
    const messaging = getCloudMessaging()
    if (messaging === null) return
    const token = await getMessagingToken(messaging)
    if (!token) return

    await axios.post(ApiPaths.DELETE_PUSH_NOTIFICATION_TOKEN, { token })
  } catch (e) {
    console.error('Failed to unsubscribe from notifications!', e)
  }
}

export async function getMessagingToken(messaging: Messaging): Promise<String | undefined> {
  const registration = await registerServiceWorker()
  if (!registration) return

  return getToken(messaging, { vapidKey: FIREBASE_WEB_PUSH_PUBLIC_KEY, serviceWorkerRegistration: registration })
}

async function unregisterServiceWorker(): Promise<void> {
  if ('serviceWorker' in navigator) {
    const registration = await navigator.serviceWorker.getRegistration(getServiceWorkerUrl())
    await registration?.unregister()
  }
}

async function registerServiceWorker(): Promise<ServiceWorkerRegistration | undefined> {
  if ('serviceWorker' in navigator) {
    const serviceWorker = await navigator.serviceWorker.register(getServiceWorkerUrl())
    await navigator.serviceWorker.ready

    return serviceWorker
  }
}

function getServiceWorkerUrl() {
  const firebaseConfig = new URLSearchParams(Object.entries(getFirebaseConfig()))
  return new URL(`/firebase-messaging-sw.js?${firebaseConfig}`, window.origin).toString()
}
