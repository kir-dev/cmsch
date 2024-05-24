importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js')
importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-messaging.js')

const urlParams = new URLSearchParams(location.search)
const firebaseConfig = Object.fromEntries(urlParams.entries())
firebase.initializeApp(firebaseConfig)

if (firebase.messaging.isSupported()) {
  const messaging = firebase.messaging()
  messaging.onBackgroundMessage((payload) => {
    const notificationTitle = payload.notification.title
    const notificationOptions = {
      body: payload.notification.body,
      tag: notificationTitle,
      icon: payload.notification?.image
    }
    self.registration.showNotification(notificationTitle, notificationOptions)
  })
}
