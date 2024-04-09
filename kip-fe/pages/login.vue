<script lang="ts" setup>
const BASE_URL = import.meta.env.VITE_BASE_URL;
const employeeId = ref();
const password = ref();
const users = ref();

async function login() {
  try {
    await userLogin(employeeId.value, password.value);
  } catch (error) {
    console.error(error);
  }
}
async function userLogin(employeeId: string, password: string) {
  console.log(JSON.stringify({employeeId, password} ));
  const response = await fetch(`${BASE_URL}/user/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({employeeId, password})
  });

  if (!response.ok) {
    throw new Error('Login failed');
  }

  const data = await response.json();
  console.log(data);
  if (typeof window !== "undefined") { // CSR 인경우만 동작함.
    window.localStorage.setItem('accessToken', data.result.access_token);
  }
}
async function getUserList() {
  const accessToken = window.localStorage.getItem('accessToken');
  const response = await fetch(`${BASE_URL}/user/mypage`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + accessToken
    },
  });
  console.log("Respose",response.ok)    // 응답값 체크

  const data = await response.json()   // 순수한 결과 값 출력.
  users.value = data;   // Vue 3.2? 부터는 setup 사용하고 이렇게 value 값으로 값을 지정함.
  console.log(data)  // 순수한 리스트
}
</script>

<template>
  <form @submit.prevent="login">
    <label>
      employeeId
      <input type="text" v-model="employeeId" required>
    </label>
    <label>
      Password
      <input type="password" v-model="password" required>
    </label>
    <button type="submit">Login</button>
  </form>
  <button @click="getUserList">유저 정보 가지고 오기</button>
  <ul>
    <li v-for="user in users" :key="user.id">{{ user.name }}</li>
  </ul>
  {{users}}
</template>