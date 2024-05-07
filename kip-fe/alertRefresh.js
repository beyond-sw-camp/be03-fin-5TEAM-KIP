const notification = useNotification();

async function refreshNotification() {
    await notification.setMyNotification();
}

export default refreshNotification;
