const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useNotification = defineStore("notifications", {
  state() {
    return {
      notifications: [],
    };
  },
  getters: {
    // getter를 업데이트하여 모든 정보를 반환
    getNotification(state){
      return state.notifications
    },
    getUnreadNotificationCount(state) {
      return state.notifications.filter(notification => notification.isRead === 'N').length;
    },
  },

  actions: {
    async setMyNotification() {
      try {
        const response = await fetch(`${BASE_URL}/notification`, {
          method: 'GET',
          headers: {'Authorization': 'Bearer ' + user.getAccessToken},
        });
        if (!response.ok) throw new Error('Failed to fetch notifications.');
        this.notifications = await response.json();
      } catch (error) {
        console.error('Error fetching notifications:', error.message);
        this.notifications = []; // 에러가 발생한 경우 북마크 목록을 초기화
      }
    },

    async removeNotification(notificationId) {
      try {
        const response = await fetch(`${BASE_URL}/notification/${notificationId}`, {
          method: 'DELETE',
          headers: {
            Accept: "application/json",
            'Authorization': 'Bearer ' + user.getAccessToken },
        });
        const data = await response.json();
        if (response.ok) {
          console.log("?")
          this.notifications = this.notifications.filter(notification => notification.noteId !== notificationId);
        } else {
          console.error('Failed to delete the notification:', data.message);
        }
      } catch (error) {
        console.error('Error while deleting the notification:', error);
      }
    },
    async readNotification(notificationId) {
      try {
        const response = await fetch(`${BASE_URL}/notification/${notificationId}`, {
          method: 'GET',
          headers: { 'Authorization': 'Bearer ' + user.getAccessToken },
        });
        const data = await response.json();
        if (response.ok) {
          const index = this.notifications.findIndex(notification => notification.id === notificationId);
          if (index !== -1) {
            // isRead 속성을 'Y'로 변경
            this.notifications[index].isRead = 'Y';
          }
        } else {
          console.error('Failed to delete the notification:', data.message);
        }
      } catch (error) {
        console.error('Error while deleting the notification:', error);
      }
    },
  }
});
