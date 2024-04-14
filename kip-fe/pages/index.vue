<script setup>
definePageMeta({
  layout: "plain"
})

const empolyeeIdInput = ref();

// Stores
const user = useUser();
const color = useColor();

async function handleSubmit(data) {
  user.login(data.empolymentId, data.password)
  await wait(2000);
  await useRouter().push('/kip');
}

// DB에 존재하는 ID 인지 검사.
async function id_check(employeeId) {
  user.isExistId = false
  let regex = /^k-\d{10}$/;  // 정규식과 일치할때만 서버에 요청하여 검사
  if(regex.test(employeeId.value))
    await user.isExistEmployeeId(employeeId)
  else return true; // 정규식 오류 메시지를 불러오기위해 항상 true로 놓음.
  return user.getIsExistId;
}
</script>



<template>
  <v-sheet class="login__sheet">
    <v-container class="login__container">
      <v-card
          class="login__card mx-auto pa-13 pb-5 mb-5"
          elevation="18"
          min-width="400"
          min-height="500">
        <v-img
            class="mx-auto"
            max-width="20vh"
            src="/images/logos/kiplogo.svg"/>

        <FormKit
            type="form"
            submit-behavior="live"
            submit-label=" 환영합니다"
            :actions="user.getIsExistId && empolyeeIdInput.length === 12"
            @submit="handleSubmit">

          <!--아이디 입력 받는 부분 -->
          <FormKit
              v-model="empolyeeIdInput"
              placeholder="Empolyment ID"
              validation="id_check|matches:/^k-\d{10}$/"
              validation-visibility="live"
              :validation-rules="{id_check}"
              type="text"
              name="empolymentId"/>

          <!--비밀번호 입력 받는 부분 길이도 12, 존재도 해야 열림-->
          <FormKit
              v-if="user.getIsExistId && empolyeeIdInput.length === 12"
              placeholder="Password"
              type="password"
              name="password"/>

        </FormKit>

      </v-card>
    </v-container>
  </v-sheet>
</template>

<style>

.login__sheet {
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