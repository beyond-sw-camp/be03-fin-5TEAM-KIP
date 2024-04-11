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
                  <img :src="profilePhotoUrl" alt="profile" />
                </v-avatar>
                <v-btn color="primary" class="ma-2">Upload</v-btn>
                <v-btn color="error" class="ma-2">Reset</v-btn>
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
                <v-btn color="primary">Save</v-btn>
                <v-btn color="grey">Cancel</v-btn>
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
                    <v-text-field label="Your Name" outlined dense></v-text-field>
                    <v-text-field label="Store Name" outlined dense></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field label="Email" outlined dense></v-text-field>
                    <v-text-field label="Phone" outlined dense></v-text-field>
                  </v-col>
                </v-row>
              </v-card-text>
              <v-card-actions class="justify-end">
                <v-btn color="primary">Save</v-btn>
                <v-btn color="grey">Cancel</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      profilePhotoUrl: '', // 프로필 사진 URL을 여기에 추가하세요
      showPassword: false,
      password: {
        current: '',
        new: '',
        confirm: '',
      },
    };
  },
  methods: {
    uploadPhoto() {
      // 사진 업로드 로직 처리
    },
    resetPhoto() {
      // 사진을 기본값으로 리셋
    },
    saveDetails() {
      // 개인 정보 저장
    },
    cancelEdit() {
      // 편집 취소
    },
    showPasswordToggle() {
      // 비밀번호 보기 토글
      this.showPassword = !this.showPassword;
    },
  },
};
</script>

<style scoped>
.caption {
  text-align: center;
  color: rgba(0, 0, 0, 0.6);
}

.ma-2 {
  margin: 8px;
}

.v-avatar.my-4 {
  margin-top: 16px;
  margin-bottom: 16px;
}

.v-btn.ma-2 {
  margin-top: 8px;
  margin-bottom: 8px;
}

.v-card-actions.justify-end {
  justify-content: flex-end;
}

.v-card-text.flex-grow-1 {
  flex-grow: 1;
}

.v-container {
  max-width: 960px;
  margin: auto;
}

@media (min-width: 1264px) {
  .v-container {
    max-width: 85%;
  }
}

/* Make sure the buttons are aligned at the bottom of the cards */
.v-card-actions {
  margin-top: auto;
}

/* Adjust the card title size */
.v-card-title {
  font-size: 1.25rem;
  font-weight: bold;
}

/* Additional styles can be placed here */
</style>