<template>
  <v-container fluid>
    <!-- 상단 탭 네비게이션 -->
    <v-row>
      <v-col>
        <v-tabs centered>
          <v-tab>Account</v-tab>
          <v-tab>BookMark</v-tab>
        </v-tabs>
      </v-col>
    </v-row>

    <!-- 메인 컨텐츠 -->
    <v-row justify="center" class="my-5">
      <v-col cols="12" md="10" lg="8">
        <!-- 프로필 변경 & 비밀번호 변경 -->
        <v-row>
          <!-- Change Profile -->
          <v-col cols="12" md="6">
            <v-card class="d-flex flex-column" outlined>
              <v-card-title class="text-h5">Change Profile</v-card-title>
              <v-card-text class="text-center flex-grow-1">
                <v-avatar size="120" class="mx-auto my-4">
                  <img :src="profilePhotoUrl || '/default-profile.png'" alt="profile" />
                </v-avatar>
                <v-btn color="primary" class="ma-2" @click="uploadPhoto">Upload</v-btn>
                <v-btn color="error" class="ma-2" @click="resetPhoto">Reset</v-btn>
                <div class="caption">Allowed JPG, GIF or PNG. Max size of 800K</div>
              </v-card-text>
            </v-card>
          </v-col>

          <!-- Change Password -->
          <v-col cols="12" md="6">
            <v-card class="d-flex flex-column" outlined>
              <v-card-title class="text-h5">Change Password</v-card-title>
              <v-card-text class="flex-grow-1">
                <v-text-field
                    v-model="password.current"
                    :type="showPassword ? 'text' : 'password'"
                    label="Current Password"
                    outlined
                    dense
                    :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                    @click:append="showPasswordToggle"
                ></v-text-field>
                <v-text-field
                    v-model="password.new"
                    :type="showPassword ? 'text' : 'password'"
                    label="New Password"
                    outlined
                    dense
                    :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                    @click:append="showPasswordToggle"
                ></v-text-field>
                <v-text-field
                    v-model="password.confirm"
                    :type="showPassword ? 'text' : 'password'"
                    label="Confirm Password"
                    outlined
                    dense
                    :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                    @click:append="showPasswordToggle"
                ></v-text-field>
              </v-card-text>
              <v-card-actions class="justify-end mt-auto">
                <v-btn color="primary" @click="saveDetails">Save</v-btn>
                <v-btn color="grey" @click="cancelEdit">Cancel</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>

        <!-- Personal Details -->
        <v-row>
          <v-col cols="12">
            <v-card outlined class="pa-4">
              <v-card-title class="text-h5">Personal Details</v-card-title>
              <v-card-text>
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field label="Your Name" outlined dense v-model="userInfo.name"></v-text-field>
                    <v-text-field label="Employee ID" outlined dense v-model="userInfo.employeeId" readonly></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field label="Email" outlined dense v-model="userInfo.email"></v-text-field>
                    <v-text-field label="Phone" outlined dense v-model="userInfo.phoneNumber"></v-text-field>
                  </v-col>
                </v-row>
              </v-card-text>
              <v-card-actions class="justify-end">
                <v-btn color="primary" @click="updateUserDetails">Save</v-btn>
                <v-btn color="grey" @click="cancelEdit">Cancel</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { useUser } from "@/stores/user";
import { ref, onMounted } from 'vue';

export default {
  data() {
    return {
      profilePhotoUrl: '',
      showPassword: false,
      password: {
        current: '',
        new: '',
        confirm: '',
      },
    };
  },
  setup() {
    const userStore = useUser();
    const userInfo = ref(userStore.userInfo);

    onMounted(async () => {
      if (!userStore.userInfo.name) {
        await userStore.fetchUserInfo();
      }
      userInfo.value = { ...userStore.userInfo };
    });

    const updateUserDetails = async () => {
      try {
        const updatedInfo = {
          name: userInfo.value.name,
          email: userInfo.value.email,
          phoneNumber: userInfo.value.phoneNumber
        };
        userStore.userInfo.name = userInfo.value.name  // 프론트로 변경
        alert("사용자 정보가 변경되었습니다.");
        // window.localStorage.setItem('refreshed', 'true'); // 리프레시 상태 저장
        // location.reload(); // 페이지 새로고침
      } catch (error) {
        console.error("Failed to update user details:", error);
        alert("Failed to update user details.");
      }
    };

    const uploadPhoto = () => {
      // 사진 업로드 로직 처리
    };

    const resetPhoto = () => {
      // 사진을 기본값으로 리셋
    };

    const saveDetails = () => {
      // 비밀번호 변경 로직 처리
    };

    const cancelEdit = () => {
      // 편집 취소
    };

    const showPasswordToggle = () => {
      // 비밀번호 보기 토글
      this.showPassword = !this.showPassword;
    };

    return {
      userInfo,
      updateUserDetails,
      uploadPhoto,
      resetPhoto,
      saveDetails,
      cancelEdit,
      showPasswordToggle,
    };
  },
};
</script>