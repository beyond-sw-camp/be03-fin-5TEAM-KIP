
<script setup>
import { ref, reactive, watch, computed } from 'vue';
import { useUser } from "@/stores/user";

const currentTab = ref('profile'); // 현재 탭 상태를 저장
const userStore = useUser();
const userInfo = ref({ ...userStore.userInfo });
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
  userInfo.value = { ...userStore.userInfo };
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
}, { deep: true });
</script>

<template>
  <v-container fluid>
    <!-- 탭 네비게이션 -->
    <v-row>
      <v-col>
        <v-tabs v-model="currentTab" centered>
          <v-tab key="profile" value="profile">프로필 변경</v-tab>
          <v-tab key="password" value="password">비밀번호 변경</v-tab>
          <v-tab key="info" value="info">개인 정보</v-tab>
        </v-tabs>
      </v-col>
    </v-row>

    <!-- 탭 컨텐츠 -->
    <v-row justify="center" class="my-5">
      <v-col cols="12" md="10" lg="8">
        <v-fade-transition mode="out-in">
          <!-- 프로필 변경 탭 내용 -->
          <div v-if="currentTab === 'profile'" :key="'profile'">
            <v-card class="my-card" outlined>
              <v-card-title class="text-h5">프로필 변경</v-card-title>
              <v-card-text class="text-center flex-grow-1">
                <v-avatar size="120" class="mx-auto my-4">
                  <img :src="profilePhotoUrl || '/default-profile.png'" alt="profile" />
                </v-avatar>
                <v-file-input
                    v-model="file"
                    label="새로운 사진 업로드"
                    prepend-icon="mdi-camera"
                    accept="image/png, image/jpeg, image/gif"
                    @change="uploadPhoto"
                ></v-file-input>
                <v-btn color="error" @click="resetPhoto">리셋</v-btn>
                <div class="caption">JPG, GIF 또는 PNG만 허용됩니다. 최대 크기는 800K입니다.</div>
              </v-card-text>
              <v-card-actions class="justify-end">
                <v-btn color="primary" @click="updateProfile">프로필 업데이트</v-btn>
                <v-btn color="grey" @click="cancelEdit">취소</v-btn>
              </v-card-actions>
            </v-card>
          </div>

          <!-- 비밀번호 변경 탭 내용 -->
          <div v-if="currentTab === 'password'" :key="'password'">
            <v-card class="my-card" outlined>
              <v-card-title class="text-h5">비밀번호 변경</v-card-title>
              <v-card-text>
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
              <v-card-actions class="justify-end">
                <v-btn
                    :disabled="!canChangePassword"
                    color="primary"
                    @click="changePassword"
                >
                  비밀번호 변경
                </v-btn>
                <v-btn color="grey" @click="cancelEdit">취소</v-btn>
              </v-card-actions>
            </v-card>
          </div>

          <!-- 개인 정보 탭 내용 -->
          <div v-if="currentTab === 'info'" :key="'info'">
            <v-card class="my-card" outlined>
              <v-card-title class="text-h5">개인 정보</v-card-title>
              <v-card-text>
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field label="이름" outlined dense v-model="userInfo.name"></v-text-field>
                    <v-text-field label="사원 번호" outlined dense v-model="userInfo.employeeId" readonly></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field label="이메일" outlined dense v-model="userInfo.email"></v-text-field>
                    <v-text-field label="전화번호" outlined dense v-model="userInfo.phoneNumber"></v-text-field>
                  </v-col>
                </v-row>
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field
                        label="고용된 날짜"
                        outlined
                        dense
                        v-model="userInfo.employedDay"
                        readonly
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-card-text>
              <v-card-actions class="justify-end">
                <v-btn color="primary" @click="updateUserDetails">저장</v-btn>
                <v-btn color="grey" @click="cancelEdit">취소</v-btn>
              </v-card-actions>
            </v-card>
          </div>
        </v-fade-transition>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.my-card {
  background-color: #FFF;
  border-radius: 16px;
  box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.26);
  margin: auto; /* 가로 중앙 정렬 */
  margin-top: 100px; /* 페이지 상단으로부터의 거리 조정 */
  margin-bottom: 20px; /* 하단 여백 */
  max-width: 600px; /* 카드 박스의 최대 너비 */
  display: flex;
  flex-direction: column;
}

.caption {
  font-size: 0.875rem;
  color: grey;
}

.error-text {
  color: red;
}

.card-actions {
  margin-top: auto; /* 버튼을 아래로 밀어내기 */
  display: flex;
  justify-content: flex-end; /* 버튼을 오른쪽으로 정렬 */
}
</style>