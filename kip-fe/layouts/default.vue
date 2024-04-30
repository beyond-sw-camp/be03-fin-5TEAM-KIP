<script setup>
import SearchModal from "~/components/SearchModal.vue";
import {onKeyStroke} from '@vueuse/core'
import {useKeyModifier} from '@vueuse/core'

definePageMeta({
  middleware: ["login"]
})
import LeftNavigation from "~/components/LeftNavigation.vue";
import NotificationCopo from "~/components/NotificationCompo.vue";

// 햄버거 버튼
const drawer = ref(true);
const rail = ref(true);
const dialog = ref(false);
const alert = ref(false)

// 피니아
const user = useUser();
const color = useColor();
const group = useGroup();
const groupUser = useGroupuser();
const document = useDocumentList()

const notification = useNotification();
await notification.setMyNotification();
notification.value = notification.getNotification;
const unreadNotificationsCount = computed(() => {
  return notification.value.filter(notif => notif.isRead === 'N').length;
});
// function
const handleRailClick = () => {
  rail.value = !rail.value;
}

// 기본데이터 로그인 후 불러오기. (최초 그룹 로딩속도 향상)
onMounted(async () => {
  await group.setHierarchyInfo();
  groupUser.$reset();
  await groupUser.setUsersInfoInGroup(1);
  await document.setAdminDocumentList(1);
  await document.setPublicDocumentList();
})

// 단축키 관련
const alt = useKeyModifier('Alt')
onKeyStroke('1', () => {
  if (alt.value) {
    drawer.value = !drawer.value
  }
})
onKeyStroke(['k', 'K'], () => {
  if (alt.value)
    dialog.value = !dialog.value
})
onKeyStroke(['L', 'l'], () => {
  if (alt.value)
    alert.value = !alert.value
})

</script>

<template>
  <v-layout>

    <!--  좌측메뉴  -->
    <v-navigation-drawer
        :color="color.kipMainColor"
        v-model="drawer"
        :rail="rail"
        rail-width="71"
        width="250"
        permanent>
      <LeftNavigation @railEvent="handleRailClick"/>
    </v-navigation-drawer>


    <!--  상단메뉴  -->
    <v-app-bar
        :color="color.kipMainColor"
        class="top__header__sheet"
        height="68">
      <template #prepend> <!-- 상단 메뉴의 제일 왼쪽-->
                          <!-- 햄버거 버튼 -->
        <v-app-bar-nav-icon
            @click.stop="$event => drawer = !drawer"
        />
      </template>
      <!--부서 계층 목록 -->
      <v-breadcrumbs :items="group.getTopNaviGroupList">
        <template v-slot:divider>
          <v-icon icon="mdi-chevron-right"></v-icon>
        </template>
      </v-breadcrumbs>

      <!--가운데 공간 만듬 -->
      <v-spacer/>

      {{ user.getUserInfo.name }}님 환영합니다.

      <v-dialog v-model="dialog" max-width="600">
        <template #activator="{ props: activatorProps }">
          <v-btn
              v-bind="activatorProps"
              class="text-none"
              stacked>
            <v-icon
                icon="mdi-magnify"
                size="x-large"
            />
          </v-btn>
        </template>
        <template #default>
          <SearchModal
              @closeModal="dialog = false"
          />
        </template>
      </v-dialog>

      <!-- 알림 버튼 -->
      <v-dialog v-model="alert" max-width="600 ">
        <template #activator="{ props: activatorProps }">
          <v-btn
              v-bind="activatorProps"
              class="text-none mr-3"
              stacked>
            <v-badge
                v-if="unreadNotificationsCount > 0"
                color="error"
                :content="unreadNotificationsCount">
              <v-icon
                  icon="mdi-bell-outline"
                  size="x-large"
              />

            </v-badge>
            <v-icon
                v-else
                icon="mdi-bell-outline"
                size="x-large"
            />
          </v-btn>
        </template>

        <template #default="{ isActive }">
          <NotificationCopo
              @isActive="isActive.value = false"
          />
        </template>
      </v-dialog>


      <template #append> <!-- 상단 메뉴의 제일 오른쪽-->
        <v-menu transition="slide-y-transition">
          <template v-slot:activator="{ props }">
            <!-- 아바타 버튼 -->
            <v-avatar
                :image="user.getProfileImageUrl"
                size="54"
                v-bind="props"
                class="cursor-pointer ml-1 mr-2"/>
          </template>
          <v-list>
            <v-list-item @click="useRouter().push('/mypage');">
              <template v-slot:prepend>
                <v-icon icon="mdi-information-box-outline"/>
              </template>
              <v-list-item-title>MyPage</v-list-item-title>
            </v-list-item>

            <v-list-item @click="user.logout">
              <template v-slot:prepend>
                <v-icon icon="mdi-logout"/>
              </template>
              <v-list-item-title>LogOut</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </template>
    </v-app-bar>

    <!--  메인 페이지  -->
    <v-main>
      <div class="main__sheet">
        <nuxt-page/>
      </div>
    </v-main>

  </v-layout>
</template>

<style>

/* 좌측 메뉴 관련 CSS */
.v-navigation-drawer__content {
  background-color: white;
  margin-top: 0.8vw;
  margin-bottom: 0.8vw;
  margin-left: 0.8vw;
  border-radius: 20px !important;
  overflow: hidden;
}

/* 상단 헤더 CSS */
.top__header__sheet {
  overflow: hidden;
  box-shadow: none !important;
}

.v-toolbar__content {
  margin: 0.8vw;
  border-radius: 20px !important;
  width: 98.4% !important;
  color: var(--primary-color);
  background-color: white;
}

/* 본문 관련 CSS */
.v-main {
  background-color: var(--primary-color);
  padding-top: calc(0.8vw + 69px) !important;
  display: flex;
  min-height: 100vh;
}

.main__sheet {
  background-color: white;
  margin: 0.8vw;
  width: 98.5%;
  min-height: calc(100vh - 1.6vw - 90px);
  border-radius: 20px;
  box-sizing: border-box;
  overflow: hidden;
}
</style>