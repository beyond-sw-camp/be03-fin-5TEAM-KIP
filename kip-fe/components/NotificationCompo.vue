<script setup>

const notifications = useNotification();

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
}
const deleteNotification = async (noteId) => {
  await notifications.removeNotification(noteId);
  await notifications.setMyNotification()
}
</script>
<template>
  <v-card
      class="mx-auto"
      max-width="70vw"
      min-width="30vw"
  >
    <v-toolbar
        dark
    >
      <v-toolbar-title>알림</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn
          @click="notificationAllow">알림 받기</v-btn>
    </v-toolbar>

    <v-list two-line>
      <template v-for="(notification, index) in notification.getNotification" :key="notification.noteId">
        <v-list-item>
          <div
              class="cursor-pointer"
              @click="readNotification(notification.noteId)"
          >
            <v-list-item-title v-text="notification.message"></v-list-item-title>
            <v-list-item-subtitle v-text="notification.createdTime"></v-list-item-subtitle>
          </div>
          <template v-slot:append>
            <v-btn
                density="compact"
                size="small"
                variant="text"
                @click="deleteNotification(notification.noteId)"
                stacked
            >
              <v-icon
              icon="mdi-close"
              color="grey"></v-icon>
            </v-btn>
          </template>
        </v-list-item>
        <v-divider
            v-if="index < notifications.value.length - 1"
            :key="index"
        ></v-divider>
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
</style>
