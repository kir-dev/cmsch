importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js')
importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-messaging.js')

const urlParams = new URLSearchParams(location.search)
const firebaseConfig = Object.fromEntries(urlParams.entries())

firebase.initializeApp(firebaseConfig)
firebase.messaging() // Listen to incoming notifications by initializing messaging
