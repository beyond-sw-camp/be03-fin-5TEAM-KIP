<template>
  <v-layout class="rounded rounded-md">
    <v-app-bar
        title="Application bar"
        location="top"
        density="default"
        :color="`#2D3250`"
        :elevation="5"
    >
      <template #append>
        <v-avatar image="https://avatars.githubusercontent.com/u/123573918?v=4" size="55"/>
        <v-avatar color="info" size="55">
          <v-icon icon="mdi-account-circle" size="40"></v-icon>
        </v-avatar>
        <v-avatar color="red" size="55">
          <span class="text-h5">SJ</span>
        </v-avatar>
      </template>

    </v-app-bar>

    <v-navigation-drawer>
      <v-list
      >
        <NuxtLink to="/"><v-list-item title="HOME"/> </NuxtLink>
        <NuxtLink to="/test"><v-list-item title="TestPage"/> </NuxtLink>
      </v-list>
    </v-navigation-drawer>

    <v-main class="d-flex align-center justify-center">
        <NuxtPage/>
    </v-main>

  </v-layout>
</template>
<script setup lang="ts">
</script>