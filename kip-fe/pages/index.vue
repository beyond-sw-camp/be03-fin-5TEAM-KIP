<script setup>
const visible = ref();
const empolymentId = ref();
const password = ref();

</script>

<template>
  <v-container>
    <v-card
        class="mx-auto pa-13 pb-8"
        elevation="18"
        min-width="400">
      <v-img
          class="mx-auto my-6"
          max-width="200"
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

      <span>{{empolymentId}}{{password}}</span>
      <v-btn
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
.v-container {
  height: 90%;
  display: flex;
  align-items: center;
}

.v-card {
  width: 40%;
  border-radius: 20px !important;
}
</style>