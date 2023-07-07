## OneToOne-Chat (clone of WhatsApp part)


This app is completely written using [Firebase](https://firebase.google.com/docs/android/setup). For storage users used [Firebase Cloud Firestore](https://firebase.google.com/docs/firestore/quickstart), for chat [Firebase Realtime Database](https://firebase.google.com/docs/database) and for images [Firebase Cloud Storage](https://firebase.google.com/docs/storage/android/start), for images [Picasso](https://github.com/square/picasso).

[Realtime chat.webm](https://github.com/Erdaulet0341/OneToOne-Chat/assets/98634106/cf9a809e-7c21-454a-b80c-80b065f2ca43)


Worked realtime notification by using [FCM](https://firebase.google.com/docs/cloud-messaging/android/client). I save in database fcm token of users and when user send message to another user by using this token I send post request to fcm resp api for sending notifications.


[Push notification.webm](https://github.com/Erdaulet0341/OneToOne-Chat/assets/98634106/2b2f4d94-c65f-4afb-b9f0-28118eacf70e)
