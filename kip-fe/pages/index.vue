<script setup>
const visible = ref();
const empolymentId = ref();
const password = ref();
import AuthUserStore from "~/stores/AuthUserStore";
import { useRouter } from 'vue-router';
const router = useRouter();


const login = () => {
  AuthUserStore().userLogin(empolymentId.value, password.value);
  router.push('/pinia');
}
</script>

<template>
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
      />

      <v-btn
          @click="login"
          class="mb-8 pa-8"
          color="blue"
          size="large"
          variant="tonal"
          block
          text="LOGIN"/>
    </v-card>
  </v-container>
</template>

<style>
.login__container {
  height: 90vh;
  display: flex;
  align-items: center;
}

.login__card {
  width: 40%;
  border-radius: 20px !important;
}
</style>