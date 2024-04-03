<script setup lang="ts">
// 햄버거 버튼
const drawer = ref(true);
</script>

<template>
  <v-layout class="rounded rounded-md">
    <!--  상단메뉴  -->
    <v-app-bar
        location="top"
        density="default"
        :color="`#2D3250`"
        :elevation="5"
    >
      <template #prepend>
        <v-app-bar-nav-icon
            variant="text"
            @click.stop="$event => drawer = !drawer"
        />
        <v-toolbar-title>
          <NuxtLink to="/">
            KIP (Knowledge Is Power)
          </NuxtLink>
        </v-toolbar-title>
      </template>
      <template #append>
        <v-menu>
          <template v-slot:activator="{ props }">
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


    <!--  우측메뉴  -->
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
          <v-list-item prepend-icon="mdi-note" title="POSTING" value="shared"></v-list-item>
        </NuxtLink>
        <NuxtLink to="/comments">
          <v-list-item prepend-icon="mdi-comment" title="COMMENTS" value="starred"></v-list-item>
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
