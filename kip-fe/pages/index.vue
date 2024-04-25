<script setup>
import {useFirebaseApp} from "~/useFireBase.ts";
import {useFirebaseMessaging} from "~/useFireBaseMessaging.ts";

definePageMeta({
  layout: "plain"
})

const visible = ref(false);
const empolyeeIdInput = ref("");
const passwordInput = ref("");
let FCMToken = ref();

// Stores
const user = useUser();
const color = useColor();

onMounted(async () => {
  const firebaseApp = useFirebaseApp();
  const { fetchFCMToken} = useFirebaseMessaging(firebaseApp);
  await fetchFCMToken().then(result => FCMToken.value = result);
})



async function handleSubmit(data) {
  user.login(data.empolymentId, data.password, FCMToken.value)
  await wait(1200); // 1.2초 대기
  await useRouter().push('/publicOpenDoc');
}

// DB에 존재하는 ID 인지 검사.
async function id_check(employeeId) {
  user.isExistId = false // 검사 전에 초기화
  user.isCorrectPassword = false
  // 정규식과 일치할때만 서버에 요청하여 검사
  if (id_Regex(employeeId))
    await user.isExistEmployeeId(employeeId)
  else return true; // 정규식 오류 메시지를 불러오기위해 항상 true로 놓음.
  return user.getIsExistId;
}

function id_Regex(employeeId) {
  // 사번 정규식 적는곳
  return /^k-\d{10}$/.test(employeeId.value);
}

// 비밀번호 체크 관련함수.
async function pass_check(password) {
  user.isCorrectPassword = false // 검사전에 초기화
  if (password_Regex(password))
    await user.isPasswordCorrect(
        empolyeeIdInput.value, password.value)
  else return true
  return user.getIsCorrectPassword;
}

function password_Regex(password) {
  // 비밀번호 정규식 넣는 곳.
  return /^\d{4}$/.test(password.value);
}


// 잇풋 값이 아무것도 없을때 초기화
watch(empolyeeIdInput, handleEmptyInput);
watch(passwordInput, handleEmptyInput);

function handleEmptyInput() {
  if (empolyeeIdInput.value === "") {
    user.isExistId = false;
    user.isCorrectPassword = false
  }
  if (passwordInput.value === "")
    user.isCorrectPassword = false
}

</script>

<template>
  <v-sheet class="login__sheet">
    <v-container class="login__container">
      <v-card
          class="login__card mx-auto"
          elevation="18"
          min-width="400"
          min-height="500">

        <div class="login__box">
          <v-img
              class="login__image"
              width="20vw"
              src="/images/logos/kiplogo.svg"/>

          <FormKit
              type="form"
              submit-behavior="live"
              :submit-label="` ${user.getJustUserName}님 환영합니다 `"
              :actions="user.getIsCorrectPassword"
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

            <!--비밀번호 입력 받는 부분-->
            <FormKit
                v-model="passwordInput"
                v-if="user.getIsExistId"
                placeholder="Password"
                :suffix-icon="visible ? 'eye' : 'eyeClosed'"
                @suffix-icon-click="visible = !visible"
                suffix-icon-class="hover:text-blue-500"
                validation="pass_check|password_Regex"
                validation-visibility="live"
                :validation-rules="{pass_check, password_Regex}"
                :type="visible ? 'text' : 'password'"
                name="password"/>

          </FormKit>
        </div>
      </v-card>
    </v-container>
  </v-sheet>
</template>

<style scoped>

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
  display: flex;
  justify-content: center;
  align-items: center;
  width: 46%;
  border-radius: 20px !important;
  padding-top: 4vw;
  padding-bottom: 3vw;
}

.login__box {
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  width: 80%;
}

.formkit-form {
  flex-direction: column;
  justify-content: start;
  align-items: end;
  width: 320px;
  height: 160px;
}
.login__image {
  margin-bottom: 4vw;
}
</style>