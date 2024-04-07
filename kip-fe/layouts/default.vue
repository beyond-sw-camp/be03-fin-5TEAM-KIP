<script setup lang="ts">

import CartStore from "~/stores/CartStore";
import KipColor from "~/stores/KipColor";
import AuthUserStore from "~/stores/AuthUserStore";

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
          <v-card :title="`${AuthUserStore().username}'s Notification`"
                  class="mx-auto"
                  max-width="450"
          >
            <v-card
                v-for="product in CartStore().grouped"
                class="mx-auto"
                max-width="344"
                :subtitle="`$${product[0].price} X ${CartStore().groupCount(product[0].name)}개`"
                :title="product[0].name">


              <template v-slot:prepend>
                <v-avatar
                    :image="`/images/${product[0].image}`"
                    size="55"
                />
              </template>

              <template v-slot:append>
                <v-icon
                    icon="mdi-trash-can"
                    size="x-large"
                    @click="CartStore().deleteItems(product[0].name)"
                />
              </template>

            </v-card>

            <!--닫기버튼-->
            <v-card-actions>
              Total Price: ${{ CartStore().totalPrice }}
              <v-spacer/>
              <v-btn
                  text="X"
                  @click="isActive.value = false"
                  :color="KipColor().kipMainColor"
                  variant="flat"
              />
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
