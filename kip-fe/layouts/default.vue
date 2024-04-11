<script setup lang="ts">
definePageMeta({
  middleware: ["login"]
})
import LeftNavigation from "~/components/LeftNavigation.vue";
import NotificationCopo from "~/components/NotificationCompo.vue";

// 햄버거 버튼
const drawer = ref(true);
const rail = ref(true);

// 피니아
const user = useUser();
const cart = useCart()
const color = useColor();

// function
const handleRailClick = () => {
  rail.value = !rail.value;
}
</script>

<template>
  <v-layout>

    <!--  좌측메뉴  -->
    <v-navigation-drawer
        :color="color.kipMainColor"
        v-model="drawer"
        :rail="rail"
        width="240"
        permanent>
      <LeftNavigation @railEvent="handleRailClick"/>
    </v-navigation-drawer>


    <!--  상단메뉴  -->
    <v-app-bar
        :color="color.kipMainColor"
        class="top__header__sheet"
        height="68">
      <template #prepend>
        <!-- 햄버거 버튼 -->
        <v-app-bar-nav-icon
            @click.stop="$event => drawer = !drawer"
        />
      </template>

      <v-toolbar-title>
        <NuxtLink to="/kip">
          KIP (Knowledge Is Power)
        </NuxtLink>
      </v-toolbar-title>
      {{ user.getUserInfo.name }}님 환영합니다.
      <!-- 알림 버튼 -->
      <v-dialog max-width="600">
        <template #activator="{ props: activatorProps }">
          <v-btn
              v-bind="activatorProps"
              class="text-none"
              stacked>
            <v-badge
                v-if="!cart.isEmpty"
                color="error"
                :content="cart.count">
              <v-icon
                  icon="mdi-bell-ring"
                  size="x-large"
              />
            </v-badge>
            <v-icon
                v-else
                icon="mdi-bell"
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


      <template #append>
        <v-menu transition="slide-y-transition">
          <template v-slot:activator="{ props }">
            <!-- 아바타 버튼 -->
            <v-avatar
                image="https://avatars.githubusercontent.com/u/123573918?v=4"
                size="55"
                v-bind="props"
                class="cursor-pointer"/>
          </template>
          <v-list>
            <v-list-item @click="useRouter().push('/kip');">
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
        <slot></slot>
      </div>
    </v-main>

  </v-layout>
</template>

<style>
/* 공통컬러 불러오기 */
@import '../assets/css/color.css';

/* 좌측 메뉴 관련 CSS */
.v-navigation-drawer__content {
  background-color: white;
  margin-top: 0.8vw;
  margin-bottom: 0.8vw;
  margin-left: 0.6vw;

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
  width: 98.5% !important;
  color: var(--primary-color);
  background-color: white;
}


/* 본문 관련 CSS */
.v-main {
  background-color: var(--primary-color);
  padding-top: calc(0.8vw + 69px) !important;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.main__sheet {
  background-color: white;
  margin: 0.8vw;
  width: 98.5%;
  height: 100%;
  border-radius: 20px;
  box-sizing: border-box;
  overflow: hidden;
}
</style>