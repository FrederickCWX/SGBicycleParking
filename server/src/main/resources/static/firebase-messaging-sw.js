importScripts("https://www.gstatic.com/firebasejs/9.1.3/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/9.1.3/firebase-messaging-compat.js");

firebase.initializeApp({
 apiKey: "AIzaSyDnHzRgZ9zaJ1iplU0fGyfnMST1SbvPF2Q",
 authDomain: "bicycleparkingapp-38884.firebaseapp.com",
 //databaseURL: "config data from general tab",
 projectId: "bicycleparkingapp-38884",
 storageBucket: "bicycleparkingapp-38884.appspot.com",
 messagingSenderId: "469376678621",
 appId: "1:469376678621:web:fbd6ef7b39d95f4bc63934",
 measurementId: "G-6KGPWELEQM"
});

const messaging = firebase.messaging();