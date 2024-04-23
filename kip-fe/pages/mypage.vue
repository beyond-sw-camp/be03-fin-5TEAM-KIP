<template>
  <v-container fluid>
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
                    @input="validateCurrentPassword"
                    :error-messages="passwordErrors.current"
                ></v-text-field>
                <!-- New Password Field -->
                <v-text-field
                    v-model="password.new"
                    :type="showPassword ? 'text' : 'password'"
                    label="New Password"
                    outlined
                    dense
                    append-icon="mdi-eye"
                    @input="validateNewPassword"
                    :error-messages="passwordErrors.new"
                    required
                ></v-text-field>
                <!-- Confirm New Password Field -->
                <v-text-field
                    v-model="password.confirm"
                    :type="showPassword ? 'text' : 'password'"
                    label="Confirm New Password"
                    outlined
                    dense
                    append-icon="mdi-eye"
                    @input="validateConfirmPassword"
                    :error-messages="passwordErrors.confirm"
                    required
                ></v-text-field>
              </v-card-text>
              <v-card-actions class="justify-end">
                <!-- Change Password Button -->
                <v-btn
                    :disabled="!canChangePassword"
                    color="primary"
                    @click="changePassword"
                >
                  Change Password
                </v-btn>
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
                <!-- Employed Day Field -->
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field
                        label="Employed Day"
                        outlined
                        dense
                        v-model="userInfo.employedDay"
                        readonly
                    ></v-text-field>
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
  passwordErrors.current = [];
  if (!password.current) {
    passwordErrors.current.push("Current password is required.");
  } else {
    const isValid = await userStore.validateCurrentPassword(password.current);
    isCurrentPasswordValid.value = isValid;
    passwordMatch.value = isValid;
    passwordStatusMessage.value = isValid
        ? 'Current password is correct. You can now enter a new password.'
        : 'Current password does not match. Please try again.';
    if (!isValid) {
      passwordErrors.current.push("Current password is incorrect.");
    }
  }
};

const validateNewPassword = () => {
  passwordErrors.new = [];
  if (password.new.length < 8) {
    passwordErrors.new.push("New password must be at least 8 characters long.");
  }
};

const validateConfirmPassword = () => {
  passwordErrors.confirm = [];
  if (password.new !== password.confirm) {
    passwordErrors.confirm.push("Passwords do not match.");
  }
};

const canChangePassword = computed(() => {
  return password.current && password.new && password.confirm && (password.new === password.confirm) && passwordErrors.current.length === 0;
});

const changePassword = async () => {
  if (canChangePassword.value) {
    try {
      const success = await userStore.changePassword(password.current, password.new);
      if (success) {
        alert('Password changed successfully.');
        password.current = '';
        password.new = '';
        password.confirm = '';
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
      phoneNumber: userInfo.value.phoneNumber,
      employedDay: userInfo.value.employedDay
    });
    alert('User details updated successfully.');
  } catch (error) {
    console.error('Failed to update user details:', error);
    alert('Failed to update user details.');
  }
};

const cancelEdit = () => {
  // Reset changes
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

const toggleShowPassword = () => {
  showPassword.value = !showPassword.value;
};

watch(password, () => {
  if (password.new !== password.confirm) {
    passwordErrors.confirm = ["Passwords do not match."];
  } else {
    passwordErrors.confirm = [];
  }

  if (password.new && password.new.length < 8) {
    passwordErrors.new = ["New password must be at least 8 characters long."];
  } else {
    passwordErrors.new = [];
  }
}, { deep: true });
</script>
