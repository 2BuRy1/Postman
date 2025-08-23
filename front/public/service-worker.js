/* eslint-env serviceworker */
self.addEventListener('push', (event) => {
    console.log('🔥 PUSH EVENT received:', event);

    try {
        let data = {
            title: '📢 Уведомление',
            body: 'Новое сообщение'
        };

        if (event.data) {
            try {
                data = { ...data, ...event.data.json() };
            } catch (e) {
                data.body = event.data.text() || data.body;
            }
        }

        console.log('🎯 Showing notification:', data);

        event.waitUntil(
            self.registration.showNotification(data.title, {
                body: data.body,
                vibrate: [100, 50, 100]
            })
                .then(() => console.log('✅ Notification shown'))
                .catch(error => console.error('❌ Notification error:', error))
        );

    } catch (error) {
        console.error('💥 Push handler error:', error);
    }
});

self.addEventListener('notificationclick', (event) => {
    console.log('🖱️ Notification clicked');
    event.notification.close();
});