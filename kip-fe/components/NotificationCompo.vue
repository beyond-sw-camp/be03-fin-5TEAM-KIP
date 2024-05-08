<script setup>
const notifications = useNotifications();
const notificationAllow = () => {
  let notificationPermission = Notification.permission;
  if (notificationPermission === "granted") {
    alert("알림이 이미 허용 되었습니다.")
  }else {
    Notification.requestPermission().then((permission) => {
      if (permission !== 'granted') {
        alert("알림 허용에 실패했습니다.\n" +
            "브라우저 알림을 허용해주세요.")
      } else {
        alert("알림이 허용 되었습니다.")
      }
    });
  }
}
const readNotification = async (noteId) => {
  await notifications.readNotification(noteId)
  await notifications.setMyNotification()
  await navigateTo('/requests')
  close();
}
const deleteNotification = async (noteId) => {
  await notifications.removeNotification(noteId);
  await notifications.setMyNotification()
}

const color = useColor()
const emit = defineEmits(['closeModal']);
const close = () => {
  emit('closeModal');
}
</script>
<template>
  <v-card
      width="60vw"
      rounded='xl'
      class="mx-auto pa-7"
  >
    <v-toolbar
        dark
    >
      <v-toolbar-title class="headline"> 📨 요청 메시지  </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn
          variant="elevated"
          :color="color.kipMainColor"
          @click="notificationAllow">알림 허용</v-btn>
    </v-toolbar>
    <v-list two-line>
      <template v-for="(notice, index) in notifications.getNotification" :key="notice.noteId">
        <v-list-item>
          <div
              class="cursor-pointer"
              @click="readNotification(notice.noteId)"
          >
            <v-list-item-title
                class="mb-1"
                :class="notice.isRead !== 'Y'? 'notRead__noti':'Read__noti'"
                >
              {{ notice.message.includes("요청을 수락하셨습니다.") ?"✅":"" }}
              {{ notice.message.includes("요청을 거절하셨습니다.") ?"❌":"" }}
              {{ notice.message.includes("요청했습니다.") ?"🔔":"" }}


              {{notice.message}} </v-list-item-title>
            <v-list-item-subtitle class="mb-1" v-text="notice.createdTime"></v-list-item-subtitle>
          </div>
          <template v-slot:append>
            <v-btn
                density="compact"
                size="small"
                variant="text"
                @click="deleteNotification(notice.noteId)"
                stacked
            >
              <v-icon
              icon="mdi-close"
              color="grey"></v-icon>
            </v-btn>
          </template>
        </v-list-item>
        <v-divider></v-divider>

      </template>
    </v-list>
  </v-card>
</template>
<style scoped>
:deep(.v-toolbar__content) {
  margin: 0 !important;
  width: 100% !important;
  border-radius: 0 !important;
  border-bottom: 1px solid #ddd !important;
}
.notRead__noti {
  color:var(--primary-color) !important;
  font-weight: bold;
}
.Read__noti{
  color: #bcbcbc !important;
  font-weight: 350;
}
</style>
