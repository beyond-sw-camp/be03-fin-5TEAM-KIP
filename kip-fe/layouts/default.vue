<script setup lang="ts">

import CartStore from "~/stores/CartStore";
import KipColor from "~/stores/KipColor";

// 햄버거 버튼
const drawer = ref(true);
</script>

<template>
  <v-layout class="rounded rounded-md">

    <!--  상단메뉴  -->
    <v-app-bar
        location="top"
        density="default"
        :color="KipColor().kipMainColor"
        :elevation="5"
    >

      <template #prepend>
        <!-- 햄버거 버튼 -->
        <v-app-bar-nav-icon
            variant="text"
            @click.stop="$event => drawer = !drawer"
        />
      </template>

      <v-toolbar-title>
        <NuxtLink to="/">
          KIP (Knowledge Is Power)
        </NuxtLink>
      </v-toolbar-title>

      <!-- 알림 버튼 -->
      <v-dialog max-width="500">
        <template #activator="{ props: activatorProps }">
          <v-btn
              v-bind="activatorProps"
              class="text-none"
              stacked>
            <v-badge
                v-if="CartStore().count !== 0"
                color="error"
                :content="CartStore().count">
              <v-icon
                  icon="mdi-bell-ring"
                  size="x-large"
              /></v-badge>
            <v-icon
                v-else
                icon="mdi-bell"
                size="x-large"
            />
          </v-btn>
        </template>

        <template #default="{ isActive }">
          <v-card title="Dialog">
            <v-card-text>
              Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
              dolore magna aliqua.
            </v-card-text>

            <v-card-actions>
              <v-spacer></v-spacer>

              <v-btn
                  text="Close Dialog"
                  @click="isActive.value = false"
                  color="surface-variant"
                  variant="flat"
              ></v-btn>
            </v-card-actions>
          </v-card>
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
        expand-on-hover
        rail
        v-model="drawer"
    >
      <v-list density="compact" nav>
        <NuxtLink to="/">
          <v-list-item prepend-icon="mdi-home-circle" title="HOME" value="myfiles"/>
        </NuxtLink>
        <NuxtLink to="/posts">
          <v-list-item prepend-icon="mdi-note" title="POSTING" value="shared"/>
        </NuxtLink>
        <NuxtLink to="/comments">
          <v-list-item prepend-icon="mdi-comment" title="COMMENTS" value="COMMENTS"/>
        </NuxtLink>
        <NuxtLink to="/dialogs">
          <v-list-item prepend-icon="mdi-access-point" title="DIALOGS"/>
        </NuxtLink>
        <NuxtLink to="/login">
          <v-list-item prepend-icon="mdi-login-variant" title="LOGIN"/>
        </NuxtLink>
        <NuxtLink to="/pinia">
          <v-list-item prepend-icon="mdi-store-cog" title="PINIA"/>
        </NuxtLink>


      </v-list>
    </v-navigation-drawer>

    <!--  메인 페이지  -->
    <v-main>
      <div class="pa-5">
        <NuxtPage/>
      </div>

    </v-main>

  </v-layout>
</template>
