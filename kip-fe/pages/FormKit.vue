<script setup lang="ts">
const formData = ref({
  username: "sejongpark",
  password: "123124"
});

async function handleSubmit(data) {
  await wait(3000);
  console.log(data);
}
</script>

<template>
  <div style="display: flex; justify-content: center; align-items: start">
    <v-card>
      <FormKit
          label="Username"
          name="username"
          type="text"
          help="Pick a new username"
          validation="required|matches:/^k-{a-zA-Z]+$/|length:5"
          value="k-"
          prefix-icon="avatarMan"
          inner-class="mycustom-inner"
          input-class="hi"
      />
      <FormKit type="text"/>
      <FormKit type="select"/>
      <FormKit type="textarea"/>
    </v-card>

    <v-card>
      <FormKit type="form" :value="formData" @submit="handleSubmit">
        <h1>Login</h1>
        <FormKit type="text" label="Username" name="username"/>
        <FormKit type="password" label="Password" name="password"/>
      </FormKit>
    </v-card>

  </div>
</template>

<style scoped>
.v-card {
  padding: 30px;
  margin: 20px;
  min-width: 200px;
}
</style>