importScripts("https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js");
importScripts(
  "https://www.gstatic.com/firebasejs/8.10.0/firebase-messaging.js"
);

firebase.initializeApp({
    apiKey: "AIzaSyBvsy3isBp6lRk5-n-IIku_nuxHUeR-MHk",
    projectId: "kip-fcm",
    messagingSenderId: "960692524574",
    appId: "1:960692524574:web:12332d6b68f8f2e1bcc00f",
});

const messaging = firebase.messaging();

// 브라우저가 꺼진후에도 진행되어질수 있도록하는 method
messaging.onBackgroundMessage((payload) => {
    console.log(
      '[firebase-messaging-sw.js] Received background message ',
      payload
    );
    // Customize notification here  
    const notificationTitle = payload.data.title;
    const notificationOptions = {
      body: payload.data.content,
      icon: '/favicon.png'
    };
  
    self.registration.showNotification(notificationTitle, notificationOptions);
  });