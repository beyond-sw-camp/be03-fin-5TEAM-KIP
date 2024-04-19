<template>
  <v-container fluid>
    <v-row>
      <v-col>
        <v-tabs centered>
          <v-tab>Account</v-tab>
          <v-tab>BookMark</v-tab>
        </v-tabs>
      </v-col>
    </v-row>

    <v-row justify="center" class="my-5">
      <v-col cols="12" md="10" lg="8">
        <v-row>
          <v-col cols="12" md="6">
            <v-card class="d-flex flex-column" outlined>
              <v-card-title class="text-h5">Change Profile</v-card-title>
              <v-card-text class="text-center flex-grow-1">
                <v-avatar size="120" class="mx-auto my-4">
                  <img :src="profilePhotoUrl || '/default-profile.png'" alt="profile" />
                </v-avatar>
                <v-file-input
                    v-model="file"
                    label="Upload new photo"
                    prepend-icon="mdi-camera"
                    accept="image/png, image/jpeg, image/gif"
                    @change="uploadPhoto"
                ></v-file-input>
                <v-btn color="error" @click="resetPhoto">Reset</v-btn>
                <div class="caption">Allowed JPG, GIF or PNG. Max size of 800K</div>
              </v-card-text>
            </v-card>
          </v-col>

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
                <v-btn color="primary" @click="changePassword">Save</v-btn>
                <v-btn color="grey" @click="cancelEdit">Cancel</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12">
            <v-card outlined class="pa-4">
              <v-card-title class="text-h5">Personal Details</v-card-title>
              <v-card-text>
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field label="Your Name" outlined dense v-model="userInfo.name"></v-text-field>
                    <v-text-field label="Employee ID" outlined dense v-model="userInfo.employeeId"
                                  readonly></v-text-field>
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
import {ref, onMounted} from 'vue';
import {useUser} from "@/stores/user";

export default {
  setup() {
    const userStore = useUser();
    const userInfo = ref({...userStore.userInfo});
    const profilePhotoUrl = ref(userStore.getProfileImageUrl);
    const password = ref({
      current: '',
      new: '',
      confirm: ''
    });
    const showPassword = ref(false);

    onMounted(async () => {
      if (!userStore.userInfo.name) {
        await userStore.fetchUserInfo();
      }
      userInfo.value = {...userStore.userInfo};
    });

    const updateUserDetails = async () => {
      try {
        await userStore.updateUserInfo({
          name: userInfo.value.name,
          email: userInfo.value.email,
          phoneNumber: userInfo.value.phoneNumber
        });
        alert("User details updated successfully.");
      } catch (error) {
        console.error("Failed to update user details:", error);
        alert("Failed to update user details.");
      }
    };

    const uploadPhoto = async (file) => {
      // Upload photo logic
    };

    const resetPhoto = () => {
      // Reset photo logic
    };

    const changePassword = async () => {
      if (password.value.new !== password.value.confirm) {
        alert("New passwords do not match.");
        return;
      }
      try {
        await userStore.changePassword({
          currentPassword: password.value.current,
          newPassword: password.value.new
        });
        alert("Password changed successfully.");
      } catch (error) {
        console.error("Failed to change password:", error);
        alert("Failed to change password.");
      }
    };

    const cancelEdit = () => {
      // Cancel edit logic
    };

    const showPasswordToggle = () => {
      showPassword.value = !showPassword.value;
    };

    return {
      userInfo,
      profilePhotoUrl,
      password,
      showPassword,
      updateUserDetails,
      uploadPhoto,
      resetPhoto,
      changePassword,
      cancelEdit,
      showPasswordToggle
    };
  }
};
</script>