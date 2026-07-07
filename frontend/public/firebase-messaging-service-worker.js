import { initializeApp } from 'https://www.gstatic.com/firebasejs/12.15.0/firebase-app.js'
import { getMessaging } from 'https://www.gstatic.com/firebasejs/12.15.0/firebase-messaging-sw.js'

const urlParams = new URLSearchParams(location.search)
const firebaseConfig = Object.fromEntries(urlParams.entries())

const app = initializeApp(firebaseConfig)
const messaging = getMessaging(app)
