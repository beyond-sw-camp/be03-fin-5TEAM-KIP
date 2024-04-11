<script setup>
definePageMeta({
  layout: "plain"
})

const visible = ref();
const empolymentId = ref();
const password = ref();

// Stores
const user = useUser();
const color = useColor();


</script>

<template>
<v-sheet class="login__sheet">
  <v-container class="login__container">
    <v-card
        class="login__card mx-auto pa-13 pb-5 mb-5"
        elevation="18"
        min-width="400">
      <v-img
          class="mx-auto"
          max-width="20vh"
          src="/images/logos/kiplogo.svg"/>
      <v-card-title class="text-center">Welcome to KIP</v-card-title>

      <v-text-field
          placeholder=" Empolyment ID"
          prepend-inner-icon="mdi-key-variant"
          variant="outlined"
          class="mt-10"
          v-model="empolymentId"
          :color="color.kipSubColor"
      />
      <v-text-field
          :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
          :type="visible ? 'text' : 'password'"
          placeholder=" Enter your password"
          prepend-inner-icon="mdi-lock-outline"
          variant="outlined"
          @click:append-inner="visible = !visible"
          class="mb-5"
          v-model="password"
          :color="color.kipSubColor"
      />

      <v-btn
          @click="user.login(empolymentId, password)"
          class="mb-8 pa-8"
          color="blue"
          size="large"
          variant="tonal"
          block
          text="LOGIN"/>
    </v-card>
  </v-container>
</v-sheet>
</template>

<style>
/* 공통컬러 불러오기 */
@import '../assets/css/color.css';

.login__sheet{
  background-color: var(--primary-color) !important;
  width: 100vw;
  height: 100vh;
}
.login__container {
  height: 100vh;
  display: flex;
  align-items: center;
}

.login__card {
  width: 43%;
  border-radius: 20px !important;
}
</style>