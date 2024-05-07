<script setup>
import {useFirebaseApp} from "~/useFireBase.ts";
import {useFirebaseMessaging} from "~/useFireBaseMessaging.ts";

const rail = ref(true);
const emit = defineEmits(["railEvent"]);
const handleRailClick = () => {
  emit('railEvent');
  rail.value = !rail.value;
};

// 피니아
const group = useGroup();
const documentList = useDocumentList();
const notification = useNotification();

onMounted(async () => {
  await group.setMyGroupsInfo();  // (awit) 그룹정보를 모두 가지고 온뒤 넘어감
  await documentList.setFirstDocumentDetails();
  await notification.setMyNotification();
})

// 새로고침
const refresh = async () => {
  await group.setMyGroupsInfo()
  await group.setHierarchyInfo();
  await notification.setMyNotification();
}

const firebaseApp = useFirebaseApp();
const {onForegroundMessage} = useFirebaseMessaging(firebaseApp);
onForegroundMessage()


// 단축키
import {onKeyStroke} from '@vueuse/core'
import {useKeyModifier} from '@vueuse/core'

const alt = useKeyModifier('Alt')
onKeyStroke('2', () => {
  if (alt.value) handleRailClick()
})


</script>

<template>
  <v-sheet class="left__nav__sheet">
    <div class="d-flex justify-lg-space-between">
      <v-btn
          variant="text"
          @click="handleRailClick"
          rounded="xl"
          class="mt-4 pr-5">
        <v-icon
            variant="text"
            size="x-large"
            :icon="rail ? 'mdi-chevron-right' : 'mdi-chevron-left'"
        />
        <v-tooltip
            activator="parent"
            location="end"
            text="Alt + 2"/>
      </v-btn>
      <v-btn
          rounded="xl"
          variant="text"
          @click="refresh"
          class="mt-4 pr-5">
        <v-icon
            variant="text"
            size="large"
            icon="mdi-refresh"
        />
        <v-tooltip
            activator="parent"
            location="start"
            text="새로고침"/>
      </v-btn>
    </div>
    <v-list density="comfortable">
      <v-list-item
          title="전체공개문서"
          value="publicOpenDoc"
          to="/publicOpenDoc"
          prepend-icon="mdi-web"
          color="blue"
          rounded="xl"
          variant="text"
          class="group__list">
        <v-tooltip
            v-if="rail"
            activator="parent"
            location="end"
            text="전체공개문서"/>
      </v-list-item>

      <v-list-item
          title="부서목록"
          value="groupList"
          to="/groupList"
          prepend-icon="mdi-account-group"
          color="green"
          rounded="xl"
          variant="text"
          class="group__list">
        <v-tooltip
            v-if="rail"
            activator="parent"
            location="end"
            text="부서목록"/>
      </v-list-item>

      <v-list-item
          title="즐겨찾기"
          value="bookmarks"
          to="/bookmarks"
          prepend-icon="mdi-star"
          color="yellow-darken-2"
          rounded="xl"
          variant="text"
          class="group__list">
        <v-tooltip
            v-if="rail"
            activator="parent"
            location="end"
            text="즐겨찾기"/>
      </v-list-item>

      <v-list-item
          title="승인문서"
          value="agree"
          to="/agree"
          prepend-icon="mdi-file-document-check-outline"
          color="deep-purple"
          rounded="xl"
          variant="text"
          class="group__list">
        <v-tooltip
            v-if="rail"
            activator="parent"
            location="end"
            text="승인문서"/>
      </v-list-item>

      <v-list-item
          title="요청내역"
          value="requests"
          to="/requests"
          prepend-icon="mdi-message-outline"
          color="orange"
          rounded="xl"
          variant="text"
          class="group__list">
        <v-tooltip
            v-if="rail"
            activator="parent"
            location="end"
            text="요청내역"/>
      </v-list-item>

      <v-divider class="group__list"/>

      <!-- 부서목록 -->
      <v-list-item
          v-for="item in group.getMyGroupNamesAndId" :key="item.groupId"
          :to="`/group/${item.groupId}`"
          :prepend-icon="item.groupType === 'DEPARTMENT'
          ? 'mdi-file-account-outline' : 'mdi-alpha-b-box-outline'"
          :title="`${item.groupName} ${item.groupType === 'DEPARTMENT' ? '&nbsp 🏢' : '&nbsp 🚀'}`"
          :value="item.groupName"
          color="red"
          rounded="xl"
          variant="text"
          class="group__list">
        <v-tooltip
            v-if="rail"
            activator="parent"
            location="end"
            :text="item.groupName"/>
      </v-list-item>

    </v-list>
  </v-sheet>
</template>

<style>
.left__nav__sheet {
  color: var(--primary-color);
}

.group__list {
  margin-top: 5px;
}

.v-list-item__spacer {
  width: 13px !important;
}
</style>