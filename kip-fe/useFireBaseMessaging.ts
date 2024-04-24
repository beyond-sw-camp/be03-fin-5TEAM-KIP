import { getMessaging, getToken, onMessage } from 'firebase/messaging';
import type { FirebaseApp } from 'firebase/app';

export const useFirebaseMessaging = (firebaseApp: FirebaseApp) => {
  let messaging; // `messaging` 변수를 조건문 밖에서 선언
  if (process.client) {
    messaging = getMessaging(firebaseApp); // 클라이언트 측에서만 `messaging` 초기화
  }

  const fetchFCMToken = async () => {
    if (!messaging) return null; // `messaging`이 초기화되지 않았다면 함수를 종료
    const vapidKey = import.meta.env.VITE_FIREBASE_VAPID_KEY;
    return await getToken(messaging, { vapidKey })
    .then((currentToken) => {
      if (currentToken) {
        return currentToken;
      } else {
        console.warn('No registration token available. Request permission to generate one.');
        return null;
      }
    })
    .catch((err) => {
      console.error('An error occurred while retrieving token. ', err);
      return null;
    });
  };

  const onForegroundMessage = () => {
    if (!messaging) return; // `messaging`이 초기화되지 않았다면 함수를 종료
    onMessage(messaging, (payload) => {
      console.log('Message received. ', payload);
      new Notification(payload.data.title, {
        body: payload.data.content,
        icon: '/favicon.png',
        image: payload.data.image
      });
    });
  };

  return { fetchFCMToken, onForegroundMessage };
};
