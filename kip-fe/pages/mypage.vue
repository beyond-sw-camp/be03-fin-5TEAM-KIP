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

    <!-- Profile, Password, and Personal Details Sections -->
    <v-row justify="center" class="my-5">
      <v-col cols="12" md="10" lg="8">
        <v-row>
          <!-- Profile Section -->
          <v-col cols="12" md="6">
            <v-card class="d-flex flex-column" outlined>
              <v-card-title class="text-h5">Change Profile</v-card-title>
              <v-card-text class="text-center flex-grow-1">
                <!-- Profile Image -->
                <v-avatar size="120" class="mx-auto my-4">
                  <img :src="profilePhotoUrl || '/default-profile.png'" alt="profile" />
                </v-avatar>
                <!-- File Input for Profile Photo -->
                <v-file-input
                    v-model="file"
                    label="Upload new photo"
                    prepend-icon="mdi-camera"
                    accept="image/png, image/jpeg, image/gif"
                    @change="uploadPhoto"
                ></v-file-input>
                <!-- Reset Button -->
                <v-btn color="error" @click="resetPhoto">Reset</v-btn>
                <div class="caption">Allowed JPG, GIF or PNG. Max size of 800K</div>
              </v-card-text>
              <v-card-actions class="justify-end">
                <!-- Update Profile Button -->
                <v-btn color="primary" @click="updateProfile">Update Profile</v-btn>
                <!-- Cancel Button -->
                <v-btn color="grey" @click="cancelEdit">Cancel</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>

          <!-- Change Password Section -->
          <v-col cols="12" md="6">
            <v-card class="d-flex flex-column" outlined>
              <v-card-title class="text-h5">Change Password</v-card-title>
              <v-card-text>
                <!-- Current Password Field -->
                <v-text-field
                    v-model="password.current"
                    :type="showPassword ? 'text' : 'password'"
                    label="Current Password"
                    outlined
                    dense
                    append-icon="mdi-eye"
                    @click:append="toggleShowPassword"
                    @blur="validateCurrentPassword"
                ></v-text-field>
                <!-- Error Alert for Current Password -->
                <v-alert
                    type="error"
                    v-if="!isCurrentPasswordValid && password.current !== ''"
                    class="mt-2"
                >
                  Current password does not match.
                </v-alert>
                <!-- New Password Field -->
                <v-text-field
                    v-model="password.new"
                    :type="showPassword ? 'text' : 'password'"
                    label="New Password"
                    outlined
                    dense
                    append-icon="mdi-eye"
                ></v-text-field>
                <!-- Confirm New Password Field -->
                <v-text-field
                    v-model="password.confirm"
                    :type="showPassword ? 'text' : 'password'"
                    label="Confirm New Password"
                    outlined
                    dense
                    append-icon="mdi-eye"
                ></v-text-field>
                <!-- Change Password Button -->
                <v-btn
                    :disabled="!canChangePassword"
                    color="primary"
                    @click="changePassword"
                >
                  Change Password
                </v-btn>
              </v-card-text>
              <v-card-actions class="justify-end mt-auto">
                <!-- Cancel Button -->
                <v-btn color="grey" @click="cancelEdit">Cancel</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>

        <!-- Personal Details Section -->
        <v-row>
          <v-col cols="12">
            <v-card outlined class="pa-4">
              <v-card-title class="text-h5">Personal Details</v-card-title>
              <v-card-text>
                <v-row>
                  <!-- Name Field -->
                  <v-col cols="12" md="6">
                    <v-text-field label="Your Name" outlined dense v-model="userInfo.name"></v-text-field>
                    <v-text-field label="Employee ID" outlined dense v-model="userInfo.employeeId" readonly></v-text-field>
                  </v-col>
                  <!-- Email and Phone Fields -->
                  <v-col cols="12" md="6">
                    <v-text-field label="Email" outlined dense v-model="userInfo.email"></v-text-field>
                    <v-text-field label="Phone" outlined dense v-model="userInfo.phoneNumber"></v-text-field>
                  </v-col>
                </v-row>
              </v-card-text>
              <v-card-actions class="justify-end">
                <!-- Save Button -->
                <v-btn color="primary" @click="updateUserDetails">Save</v-btn>
                <!-- Cancel Button -->
                <v-btn color="grey" @click="cancelEdit">Cancel</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useUser } from "@/stores/user";

const userStore = useUser();
const userInfo = ref({...userStore.userInfo});
const profilePhotoUrl = ref(userStore.getProfileImageUrl);
const password = ref({
  current: '',
  new: '',
  confirm: ''
});
const showPassword = ref(false);
const isCurrentPasswordValid = ref(false);
const file = ref(null);
const passwordMatch = ref(false);
const passwordStatusMessage = ref('');

const updateProfile = async () => {
  await uploadPhoto();
};

const uploadPhoto = async () => {
  if (file.value) {
    try {
      await userStore.uploadProfileImage(file.value);
      alert('Profile image updated successfully');
    } catch (e) {
      console.error('Failed to upload new profile image:', e);
      alert('Failed to update profile image.');
    }
  }
};

const resetPhoto = () => {
  file.value = null;
  userStore.resetProfileImage();
};

const validateCurrentPassword = async () => {
  if (password.value.current) {
    try {
      const isValid = await userStore.validateCurrentPassword(password.value.current);
      isCurrentPasswordValid.value = isValid;
      passwordMatch.value = isValid;
      passwordStatusMessage.value = isValid
          ? 'Current password is correct. You can now enter a new password.'
          : 'Current password does not match. Please try again.';
    } catch (e) {
      console.error("Error validating password", e);
      passwordStatusMessage.value = 'Error during password validation.';
      isCurrentPasswordValid.value = false;
    }
  } else {
    passwordStatusMessage.value = 'Please enter your current password.';
  }
};

const canChangePassword = computed(() => {
  return passwordMatch.value && password.value.new && password.value.confirm && (password.value.new === password.value.confirm);
});

const changePassword = async () => {
  if (canChangePassword.value) {
    try {
      const success = await userStore.changePassword(password.value.current, password.value.new);
      if (success) {
        alert('Password changed successfully.');
        password.value.current = '';
        password.value.new = '';
        password.value.confirm = '';
        passwordStatusMessage.value = '';
      } else {
        alert('Failed to change password.');
      }
    } catch (error) {
      console.error('Failed to change password:', error);
      alert('Failed to change password.');
    }
  }
};

const updateUserDetails = async () => {
  try {
    await userStore.updateUserInfo({
      name: userInfo.value.name,
      email: userInfo.value.email,
      phoneNumber: userInfo.value.phoneNumber
    });
    alert('User details updated successfully.');
  } catch (error) {
    console.error('Failed to update user details:', error);
    alert('Failed to update user details.');
  }
};

const cancelEdit = () => {
  // Reset changes
  userInfo.value = {...userStore.userInfo};
  password.value.current = '';
  password.value.new = '';
  password.value.confirm = '';
  file.value = null;
  passwordStatusMessage.value = '';
};

const toggleShowPassword = () => {
  showPassword.value = !showPassword.value;
};
</script>
