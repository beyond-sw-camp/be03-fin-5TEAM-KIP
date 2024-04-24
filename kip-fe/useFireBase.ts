import { initializeApp, getApps } from 'firebase/app';
import type { FirebaseApp } from 'firebase/app';

export const useFirebaseApp = (): FirebaseApp => {
  let app: FirebaseApp;
  if (!getApps().length) {
    app = initializeApp({
      apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
      appId: import.meta.env.VITE_FIREBASE_APP_ID,
      authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
      databaseURL: import.meta.env.VITE_FIREBASE_DATABASE_URL,
      measurementId: import.meta.env.VITE_FIREBASE_MEASUREMENT_ID,
      messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
      projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
      storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
    });
  } else {
    app = getApps()[0];
  }
  return app;
};