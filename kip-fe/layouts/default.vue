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
          <v-list-item prepend-icon="mdi-comment" title="COMMENTS" value="starred"/>
        </NuxtLink>
        <NuxtLink to="/dialogs">
          <v-list-item prepend-icon="mdi-access-point" title="DIALOGS" value="starred"/>
        </NuxtLink>
        <NuxtLink to="/login">
          <v-list-item prepend-icon="mdi-login-variant" title="LOGIN" value="starred"/>
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
