<script setup lang="ts">
import CartStore from "~/stores/CartStore";
import KipColor from "~/stores/KipColor";
import LeftNavigation from "~/components/LeftNavigation.vue";
import NotificationCopo from "~/components/NotificationCopo.vue";

// 햄버거 버튼
const drawer = ref(true);
const rail = ref(true);

// function
const handleRailClick = () => {
  rail.value = !rail.value;
};

</script>

<template>
  <v-layout>

    <!--  상단메뉴  -->
    <v-app-bar :color="KipColor().kipMainColor">

      <template #prepend>
        <!-- 햄버거 버튼 -->
        <v-app-bar-nav-icon
            variant="plain"
            @click.stop="$event => drawer = !drawer"
        />
      </template>

      <v-toolbar-title>
        <NuxtLink to="/">
          KIP (Knowledge Is Power)
        </NuxtLink>
      </v-toolbar-title>

      <!-- 알림 버튼 -->
      <v-dialog max-width="600">
        <template #activator="{ props: activatorProps }">
          <v-btn
              v-bind="activatorProps"
              class="text-none"
              stacked>
            <v-badge
                v-if="!CartStore().isEmpty"
                color="error"
                :content="CartStore().count">
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
              @isActive = "isActive.value = false"
          />
        </template>
      </v-dialog>


      <template #append>
        <v-menu>
          <template v-slot:activator="{ props }">
            <!-- 아바타 버튼 -->
            <v-avatar
                image="https://avatars.githubusercontent.com/u/123573918?v=4"
                size="55"
                v-bind="props"
                class="cursor-pointer"
            />
          </template>
          <v-list>
            <v-list-item
                v-for="(item, index) in ['Profile','Setting', 'Logout']"
                :key="index"
                :value="index"
            >
              <v-list-item-title>{{ item }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

      </template>
    </v-app-bar>


    <!--  좌측메뉴  -->
    <v-navigation-drawer
        :color="KipColor().kipMainColor"
        v-model="drawer"
        :rail="rail"
        permanent>
      <LeftNavigation @railEvent="handleRailClick"/>
    </v-navigation-drawer>

    <!--  메인 페이지  -->
    <v-main>
      <NuxtPage/>
    </v-main>

  </v-layout>
</template>

<style>
/* 공통컬러 불러오기 */
@import '../assets/css/color.css';

.v-main{
  background-color: var(--primary-color);
  height: 100vh;
}
.v-sheet {
  border-radius: 20px;
}
.v-navigation-drawer__content {
  color: white;
}


</style>