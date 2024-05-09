<script setup>
import {ref, reactive, watch, computed} from 'vue';
import {useUser} from "@/stores/User";

const currentTab = ref('profile'); // 현재 탭 상태를 저장
const userStore = useUser();
const userInfo = ref({...userStore.userInfo});
const profilePhotoUrl = ref(userStore.getProfileImageUrl);
const password = reactive({
  current: '',
  new: '',
  confirm: ''
});
const showPassword = ref(false);
const isCurrentPasswordValid = ref(false);
const file = ref(null);
const passwordErrors = reactive({
  current: [],
  new: [],
  confirm: []
});

const updateProfile = async () => {
  await uploadPhoto();
};

const uploadPhoto = async () => {
  if (file.value) {
    try {
      await userStore.uploadProfileImage(file.value);
      alert('프로필 사진이 성공적으로 업데이트되었습니다.');
    } catch (e) {
      console.error('새 프로필 사진 업로드에 실패했습니다:', e);
      alert('프로필 사진 업데이트에 실패했습니다.');
    }
  }
};

const resetPhoto = () => {
  file.value = null;
  userStore.resetProfileImage();
};

const validateCurrentPassword = async () => {
  passwordErrors.current = [];
  if (!password.current) {
    passwordErrors.current.push("현재 비밀번호를 입력하세요.");
    return;
  }

  try {
    const isValid = await userStore.validateCurrentPassword(password.current);
    isCurrentPasswordValid.value = isValid;

    if (!isValid) {
      passwordErrors.current.push("현재 비밀번호가 올바르지 않습니다.");
    }
  } catch (error) {
    console.error('비밀번호 검증 중 오류 발생:', error);
    alert('비밀번호 검증 중 문제가 발생했습니다.');
  }
};

const validateNewPassword = () => {
  passwordErrors.new = [];
  const isNumeric = /^\d+$/.test(password.new);
  if (!isNumeric || password.new.length !== 4) {
    passwordErrors.new.push("비밀번호는 4자리 숫자여야 합니다.");
  }
};

const canChangePassword = computed(() => {
  return isCurrentPasswordValid.value && password.current && password.new && password.confirm && password.new === password.confirm && passwordErrors.new.length === 0 && passwordErrors.confirm.length === 0;
});

const changePassword = async () => {
  if (canChangePassword.value) {
    try {
      const success = await userStore.changePassword(password.current, password.new);
      if (success) {
        alert('비밀번호가 성공적으로 변경되었습니다.');
        password.current = ''; // 상태 초기화
        password.new = '';
        password.confirm = '';
        isCurrentPasswordValid.value = false; // 비밀번호 유효성 초기화
      } else {
        alert('비밀번호 변경에 실패했습니다.');
      }
    } catch (error) {
      console.error('비밀번호 변경 실패:', error);
      alert('비밀번호 변경에 실패했습니다.');
    }
  }
};

const updateUserDetails = async () => {
  try {
    await userStore.updateUserInfo({
      name: userInfo.value.name,
      email: userInfo.value.email,
      phoneNumber: userInfo.value.phoneNumber,
      employedDay: userInfo.value.employedDay
    });
    alert('사용자 정보가 성공적으로 업데이트되었습니다.');
  } catch (error) {
    console.error('사용자 정보 업데이트 실패:', error);
    alert('사용자 정보 업데이트에 실패했습니다.');
  }
};

const cancelEdit = () => {
  userInfo.value = {...userStore.userInfo};
  password.current = '';
  password.new = '';
  password.confirm = '';
  file.value = null;
};

const toggleShowPassword = () => {
  showPassword.value = !showPassword.value;
};

watch(password, () => {
  if (password.new !== password.confirm) {
    passwordErrors.confirm = ["비밀번호가 일치하지 않습니다."];
  } else {
    passwordErrors.confirm = [];
  }
}, {deep: true});
</script>

<template>
  <!-- 탭 네비게이션 -->
  <v-row class="px-10 py-6 mt-2">
    <v-col>
      <v-tabs v-model="currentTab" centered>
        <v-tab
            class="px-16"
            style="font-size: 18px"
            key="info" value="info">ℹ️ 개인 정보
          <v-tooltip
              activator="parent"
              location="bottom"
          >읽기전용
          </v-tooltip>
        </v-tab>
        <v-tab
            class="px-16"
            style="font-size: 18px"
            key="password" value="password"> 🗝️ 비밀번호 변경
          <v-tooltip
              activator="parent"
              location="bottom"
          >읽기전용
          </v-tooltip>
        </v-tab>
      </v-tabs>
    </v-col>
  </v-row>

  <!-- 탭 컨텐츠 -->
  <v-row justify="center" class="my-5">
    <v-col cols="12" md="10" lg="8">
      <v-fade-transition mode="out-in">
        <!-- 개인 정보 탭 내용 -->
        <div v-if="currentTab === 'info'" :key="'info'">
          <v-card rounded="xl" class="my-card" outlined>
            <v-card-title class="headline">개인 정보 ℹ️</v-card-title>
            <v-row>
              <v-col cols="6" class="d-flex flex-row justify-center align-center">
                <v-img
                    rounded="xl"
                    class="align-end text-white mx-16"
                    style="
                  height: 250px !important;
                  width: 250px !important;
                  "
                    :src="userStore.getProfileImageUrl"
                    cover
                />
              </v-col>
              <v-col cols="6">
                <v-card-text class="mr-10">
                  <!-- 이름 필드 -->
                  <v-text-field label="이름" outlined dense v-model="userInfo.name"></v-text-field>
                  <!-- 사원 번호 필드 -->
                  <v-text-field label="사원 번호" outlined dense v-model="userInfo.employeeId" readonly></v-text-field>
                  <!-- 이메일 필드 -->
                  <v-text-field label="이메일" outlined dense v-model="userInfo.email"></v-text-field>
                  <!-- 전화번호 필드 -->
                  <v-text-field label="전화번호" outlined dense v-model="userInfo.phoneNumber"></v-text-field>
                  <!-- 고용된 날짜 필드 -->
                  <v-text-field
                      label="고용된 날짜"
                      outlined
                      dense
                      v-model="userInfo.employedDay"
                      readonly
                  ></v-text-field>
                </v-card-text>
              </v-col>
            </v-row>
          </v-card>
        </div>

        <!-- 비밀번호 변경 탭 내용 -->
        <div v-if="currentTab === 'password'" :key="'password'">
          <v-card rounded="xl" class="my-card" outlined>
            <v-card-title class="headline mb-7">비밀번호 변경 🗝️</v-card-title>
            <v-card-text class="headline my-4 mx-10">
              <v-text-field
                  v-model="password.current"
                  :type="showPassword ? 'text' : 'password'"
                  label="현재 비밀번호"
                  outlined
                  dense
                  append-icon="mdi-eye"
                  @click:append="toggleShowPassword"
                  @input="validateCurrentPassword"
                  :error-messages="passwordErrors.current"
              ></v-text-field>
              <v-text-field
                  v-model="password.new"
                  :type="showPassword ? 'text' : 'password'"
                  label="새로운 비밀번호"
                  outlined
                  dense
                  append-icon="mdi-eye"
                  @input="validateNewPassword"
                  :error-messages="passwordErrors.new"
                  required
              ></v-text-field>
              <v-text-field
                  v-model="password.confirm"
                  :type="showPassword ? 'text' : 'password'"
                  label="새로운 비밀번호 확인"
                  outlined
                  dense
                  append-icon="mdi-eye"
                  @input="validateConfirmPassword"
                  :error-messages="passwordErrors.confirm"
                  required
              ></v-text-field>
            </v-card-text>

          </v-card>
        </div>


      </v-fade-transition>
    </v-col>
  </v-row>
</template>

<style scoped>


.my-card {
  max-width: 60vw; /* 모든 카드 박스의 최대 너비 */
  width: 100%; /* 모든 카드의 너비를 부모 요소의 100%로 설정 */
  padding: 30px; /* 카드 내부의 패딩 */
  display: flex;
  flex-direction: column;
}

/* 나머지 스타일 */
.caption {
  font-size: 0.875rem; /* 부가 설명 텍스트의 폰트 크기 */
  color: grey; /* 폰트 색상 */
}

.error-text {
  color: red; /* 오류 메시지 텍스트 색상 */
}

.card-actions {
  margin-top: auto; /* 카드 액션 버튼을 하단에 정렬 */
  display: flex;
  justify-content: flex-end; /* 버튼을 우측에 정렬 */
}
</style>

