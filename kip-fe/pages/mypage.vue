<template>
  <v-container fluid>
    <v-row justify="center" class="my-5">
      <v-col cols="12" md="10" lg="8">
        <v-row>
          <!-- 프로필 섹션 -->
          <v-col cols="12" md="6">
            <v-card class="d-flex flex-column" outlined>
              <v-card-title class="text-h5">프로필 변경</v-card-title>
              <v-card-text class="text-center flex-grow-1">
                <!-- 프로필 이미지 -->
                <v-avatar size="120" class="mx-auto my-4">
                  <img :src="profilePhotoUrl || '/default-profile.png'" alt="profile" />
                </v-avatar>
                <!-- 프로필 사진 업로드를 위한 파일 입력 -->
                <v-file-input
                    v-model="file"
                    label="새로운 사진 업로드"
                    prepend-icon="mdi-camera"
                    accept="image/png, image/jpeg, image/gif"
                    @change="uploadPhoto"
                ></v-file-input>
                <!-- 리셋 버튼 -->
                <v-btn color="error" @click="resetPhoto">리셋</v-btn>
                <div class="caption">JPG, GIF 또는 PNG만 허용됩니다. 최대 크기는 800K입니다.</div>
              </v-card-text>
              <v-card-actions class="justify-end">
                <!-- 프로필 업데이트 버튼 -->
                <v-btn color="primary" @click="updateProfile">프로필 업데이트</v-btn>
                <!-- 취소 버튼 -->
                <v-btn color="grey" @click="cancelEdit">취소</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>

          <!-- 비밀번호 변경 섹션 -->
          <v-col cols="12" md="6">
            <v-card class="d-flex flex-column" outlined>
              <v-card-title class="text-h5">비밀번호 변경</v-card-title>
              <v-card-text>
                <!-- 현재 비밀번호 입력 필드 -->
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
                <!-- 새로운 비밀번호 입력 필드 -->
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
                <!-- 새로운 비밀번호 확인 입력 필드 -->
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
                <!-- 비밀번호 변경 버튼 -->
                <v-btn
                    :disabled="!canChangePassword"
                    color="primary"
                    @click="changePassword"
                >
                  비밀번호 변경
                </v-btn>
                <!-- 취소 버튼 -->
                <v-btn color="grey" @click="cancelEdit">취소</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>

        <!-- 개인 정보 섹션 -->
        <v-row>
          <v-col cols="12">
            <v-card outlined class="pa-4">
              <v-card-title class="text-h5">개인 정보</v-card-title>
              <v-card-text>
                <v-row>
                  <!-- 이름 입력 필드 -->
                  <v-col cols="12" md="6">
                    <v-text-field label="이름" outlined dense v-model="userInfo.name"></v-text-field>
                    <v-text-field label="사원 번호" outlined dense v-model="userInfo.employeeId" readonly></v-text-field>
                  </v-col>
                  <!-- 이메일 및 전화번호 입력 필드 -->
                  <v-col cols="12" md="6">
                    <v-text-field label="이메일" outlined dense v-model="userInfo.email"></v-text-field>
                    <v-text-field label="전화번호" outlined dense v-model="userInfo.phoneNumber"></v-text-field>
                  </v-col>
                </v-row>
                <!-- 고용된 날짜 입력 필드 -->
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
                <!-- 저장 버튼 -->
                <v-btn color="primary" @click="updateUserDetails">저장</v-btn>
                <!-- 취소 버튼 -->
                <v-btn color="grey" @click="cancelEdit">취소</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue';
import { useUser } from "@/stores/user";

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
const passwordMatch = ref(false);
const passwordStatusMessage = ref('');
const passwordErrors = reactive({
  current: [],
  new: [],
  confirm: []
});

// 프로필 업데이트 함수
const updateProfile = async () => {
  await uploadPhoto();
};

// 프로필 사진 업로드 함수
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

// 프로필 사진 리셋 함수
const resetPhoto = () => {
  file.value = null;
  userStore.resetProfileImage();
};

// 현재 비밀번호 유효성 검사 함수
const validateCurrentPassword = async () => {
  passwordErrors.current = [];
  if (!password.current) {
    passwordErrors.current.push("현재 비밀번호를 입력하세요.");
    return; // 입력이 없으면 여기서 함수 종료
  }

  try {
    const isValid = await userStore.validateCurrentPassword(password.current);
    isCurrentPasswordValid.value = isValid; // 비동기 검증 결과를 상태에 반영

    if (!isValid) { // 유효하지 않은 경우 오류 메시지 추가
      passwordStatusMessage.value = '비밀번호가 일치하지 않습니다. 다시 시도해주세요.';
      passwordErrors.current.push("현재 비밀번호가 올바르지 않습니다.");
    } else {
      passwordStatusMessage.value = '현재 비밀번호가 일치합니다. 새로운 비밀번호를 입력할 수 있습니다.';
    }
  } catch (error) {
    console.error('비밀번호 검증 중 오류 발생:', error);
    alert('비밀번호 검증 중 문제가 발생했습니다.');
  }
};

// 새로운 비밀번호 유효성 검사 함수
const validateNewPassword = () => {
  passwordErrors.new = [];
  // 새 비밀번호가 4자리 숫자인지 확인
  const isNumeric = /^\d+$/.test(password.new);
  if (!isNumeric || password.new.length !== 4) {
    passwordErrors.new.push("비밀번호는 4자리 숫자여야 합니다.");
  }
};

// 새로운 비밀번호 확인 유효성 검사 함수
const validateConfirmPassword = () => {
  passwordErrors.confirm = [];
  if (password.new !== password.confirm) {
    passwordErrors.confirm.push("비밀번호가 일치하지 않습니다.");
  } else {
    passwordErrors.confirm = [];
  }
};

// 비밀번호 변경 가능 여부 계산
const canChangePassword = computed(() => {
  return isCurrentPasswordValid.value &&
      password.current &&
      password.new &&
      password.confirm &&
      password.new === password.confirm &&
      passwordErrors.current.length === 0 &&
      passwordErrors.new.length === 0 &&
      passwordErrors.confirm.length === 0;
});

// 비밀번호 변경 함수
const changePassword = async () => {
  if (canChangePassword.value) {
    try {
      const success = await userStore.changePassword(password.current, password.new);
      if (success) {
        alert('비밀번호가 성공적으로 변경되었습니다.');
        password.current = '';
        password.new = '';
        password.confirm = '';
        passwordStatusMessage.value = '';
      } else {
        alert('비밀번호 변경에 실패했습니다.');
      }
    } catch (error) {
      console.error('비밀번호 변경 실패:', error);
      alert('비밀번호 변경에 실패했습니다.');
    }
  }
};

// 사용자 정보 업데이트 함수
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

// 수정 취소 함수
const cancelEdit = () => {
  // 변경 사항 초기화
  userInfo.value = { ...userStore.userInfo };
  password.current = '';
  password.new = '';
  password.confirm = '';
  file.value = null;
  passwordStatusMessage.value = '';
  passwordErrors.current = [];
  passwordErrors.new = [];
  passwordErrors.confirm = [];
};

// 비밀번호 보기 토글 함수
const toggleShowPassword = () => {
  showPassword.value = !showPassword.value;
};

// 비밀번호 변경 및 새 비밀번호 일치 여부 감시
watch(password, () => {
  if (password.new !== password.confirm) {
    passwordErrors.confirm = ["비밀번호가 일치하지 않습니다."];
  } else {
    passwordErrors.confirm = [];
  }

  if (password.new && password.new.length !== 4) {
    passwordErrors.new = ["비밀번호는 4자리 숫자여야 합니다."];
  } else {
    passwordErrors.new = [];
  }
}, { deep: true });
</script>