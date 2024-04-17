<script setup>
import {VTreeview} from 'vuetify/labs/VTreeview'


// 피니아
const user = useUser();
const group = useGroup();
const color = useColor();
const groupUser = useGroupuser();

// 상단 네비 제목 설정
group.TopNaviGroupList = ["Knowledge is Power", "부서목록", "타 부서 문서와 구성원을 조회할 수 있습니다. 🥩️"];

// 데이터 세팅
await group.setHierarchyInfo();
const groups = group.getHierarchyInfo;

// 회원가입 관련 세팅
const loading = ref(false);
const open = ref();
const clickedGroupId = ref(1);
const addNewMemberModdal = ref();
const createMemberModdal = ref();


// 그릅 유저 정보 초기화
groupUser.$reset();
await groupUser.setUsersInfoInGroup(clickedGroupId.value);


// 신규계정 관련

const showPassword = ref(false)
const showPasswordConfirm = ref(false);

const data = ref({
  name: '',
  password: '',
  employeeId: '',
  employedDay: '',
  phoneNumber: '',
  empoly: '',
  email: '',
  passwordConfirm: '',

  rules: {
    required: value => !!value || '입력이 필요합니다.',
    passwordRule: value => /^\d{4}$/.test(value) || '숫자 4자리로 입력해주세요.',
    employeeIdRule: value => /^k-\d{10}$/.test(value) || '사번은 k- 포함 숫자 12자리 입니다',
    dupulicateCheck: async value => {
      await user.isExistEmployeeIdForCreate(value)
      return !user.getIsExistId || '이미 존재하는 사번입니다.'
    },


    passwordConfim: value => data.value.password === value || '비밀번호가 일치하지 않습니다.',
    email: value => {
      const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
      return pattern.test(value) || '이메일 형식이 아닙니다.'
    }
    },


  select: null,
  items: [
    'Item 1',
    'Item 2',
    'Item 3',
    'Item 4',
  ],

  checkbox: false,
});


const validate = async () => {
  const {valid} = await this.$refs.form.validate()

  if (valid) alert('Form is valid')
};

const reset = () => {
  this.$refs.form.reset()
};

const resetValidation = () => {
  this.$refs.form.resetValidation()
};


//
const setUsersInfoInGroup = async (groupId) => {
  clickedGroupId.value = groupId
  await groupUser.setUsersInfoInGroup(groupId);
}

const setAllUserInfoInKip = async () => {
  // 모달창 열고
  addNewMemberModdal.value = true
  // 현재 그룹에 없는 회원들만 조회
  await groupUser.setAllUserInfoInKip();
}

const deleteUserFromGroup = async (groupId, userId) => {
  await groupUser.deleteUserFromGroup(groupId, userId)
  await setUsersInfoInGroup(clickedGroupId.value);
  await groups.setMyGroupsInfo(); // 이게 작동 안함.
}

const addUserToGroup = async (userId) => {
  await groupUser.addUserToGroup(clickedGroupId.value, userId);
  await setUsersInfoInGroup(clickedGroupId.value);
  await group.setMyGroupsInfo();
}

</script>

<template>
  <v-container fluid>

    <!--      ✅ 그룹에 사원 추가를 위한 다이얼로그 -->
    <v-dialog
        width="70vw"
        height="80vh"
        opacity="15%"
        v-model="addNewMemberModdal">
      <v-sheet
          rounded="xl"
          class="d-flex justify-center flex-wrap pa-10">
        <v-card
            width="100%"
            class="mb-5 ml-5"
            min-width="100"
            max-width="240"
            rounded="xl"
            elevation="5"
        >
          <v-img
              class="align-end text-white"
              height="200"
              :src="`/images/profile/user${Math.ceil((Math.random() * 14))}.jpg`"
              cover
          >
          </v-img>
          <v-card-title
              v-text="`❤️ ${groupUser.getGroupName}`"/>
          <v-card-subtitle
              v-text="groupUser.getGroupType === 'DEPARTMENT' ? '🏢 부서조직': '🚀 NewBiz팀' "/>

          <v-card-actions class="d-flex justify-center">
            <!--              신규 팀원 추가 버튼-->
            <v-btn
                @click="createMemberModdal=true"
                variant="elevated"
                color="blue-lighten-1"
                class="ma-2 px-4"
                text="신규계정생성"/>
          </v-card-actions>
        </v-card>

        <!--           그룹에 소속된 회원 리스트-->
        <v-card
            width="100%"
            v-for="user in groupUser.getAllUserInfoInKip"
            :key="user.userId"
            class="mb-5 ml-5"
            min-width="100"
            max-width="240"
            rounded="xl"
            elevation="5"
        >
          <v-img
              class="align-end text-white"
              height="200"
              :src="user.profileImageUrl"
              cover
          >
          </v-img>
          <v-card-title v-text="`🐋 ${user.name} `"/>
          <v-card-subtitle v-text="`📞 ${user.phoneNumber}`"/>


          <v-card-actions class="d-flex justify-center">
            <v-btn
                @Click="addUserToGroup(user.userId)"
                variant="elevated"
                color="deep-purple-lighten-1"
                class="ma-2 px-3"
                :text="` ➕ 팀원 추가 ➕`"/>
          </v-card-actions>
        </v-card>
      </v-sheet>
      <template v-slot:actions>
        <v-btn
            class="ms-auto"
            text="Ok"
            @click="addNewMemberModdal = false"
        ></v-btn>
      </template>
    </v-dialog>


    <!--   🥩 신규 회원 생성을 위한 다이얼로그 -->
    <v-dialog
        class="d-flex justify-center"
        width="50vw"
        height="70vh"
        opacity="15%"
        v-model="createMemberModdal">

      <v-sheet
          width="50vw"
          height="70vh"
          rounded="xl"
          class="d-flex justify-center flex-wrap pa-10">

        <!--           ❤️ 그룹에 소속된 회원 리스트-->
        <v-form ref="form">

          <v-text-field
              label="사번 ( k-1234567890 )"
              v-model="data.employeeId"
              :rules="[data.rules.employeeIdRule, data.rules.dupulicateCheck]"
              clearable
              maxlength="12"
              required
              counter
          />

          <v-text-field
              label="이름"
              v-model="data.name"
              :rules="[data.rules.required]"
              clearable
              required
          />
          <v-text-field
              label="비밀번호"
              v-model="data.password"
              :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
              :rules="[data.rules.passwordRule]"
              :type="showPassword ? 'text' : 'password'"
              hint="4자리 숫자"
              maxlength="4"
              name="input-10-1"
              counter
              @click:append="showPassword = !showPassword"
          />
          <v-text-field
              label="비밀번호 확인"
              v-model="data.passwordConfirm"
              :append-icon="showPasswordConfirm ? 'mdi-eye' : 'mdi-eye-off'"
              :rules="[data.rules.passwordConfim]"
              :type="showPasswordConfirm ? 'text' : 'password'"
              maxlength="4"
              hint="4자리 숫자"
              name="input-10-1"
              counter
              @click:append="showPasswordConfirm = !showPasswordConfirm"
          />
          <v-text-field
              v-model="data.phoneNumber"
              :rules="data.nameRules"
              clearable
              label="연락처"
              required
          />
          <v-text-field
              v-model="data.email"
              :rules="[data.rules.email]"
              clearable
              label="이메일"
              required
          />
          <v-text-field
              v-model="data.employedDay"
              :rules="data.nameRules"
              clearable
              label="입사일"
              required
          />




          <div class="d-flex">
            <v-btn
                class="mt-4"
                color="success"
                @click="validate"
            >
              Validate
            </v-btn>

            <v-btn
                class="mt-4"
                color="error"
                @click="reset"
            >
              Reset Form
            </v-btn>
            <v-btn
                class="mt-4"
                color="warning"
                @click="resetValidation"
            >
              Reset Validation
            </v-btn>
          </div>
        </v-form>
      </v-sheet>
      <template v-slot:actions>
        <v-btn
            class="ms-auto"
            text="Ok"
            @click="addNewMemberModdal = false"
        ></v-btn>
      </template>
    </v-dialog>

    <!--    ☎️ 실제 본문 -->
    <v-row justify="center">

      <!--          👈 왼쪽 조직 리스트 -->
      <v-col cols="4" class="pl-8">
        <v-sheet>
          <v-card
              elevation="5"
              rounded="xl">
            <v-card-text>
              <v-treeview
                  v-model:open="open"
                  :filter="filter"
                  :items="groups"
                  color=""
              >
                <template v-slot:prepend="{ item }">
                  <v-icon
                      v-if="item.children"
                      :icon="`mdi-${item.children.length === 0
                                ? 'account-group-outline' : 'folder-network'}`"
                      @click="setUsersInfoInGroup( item.id)"
                  ></v-icon>
                </template>
                <template v-slot:title="{ item }">
                  <div @click="setUsersInfoInGroup( item.id)">
                    {{ item.title }}
                  </div>
                </template>
              </v-treeview>
            </v-card-text>
          </v-card>
        </v-sheet>
      </v-col>

      <!--        👉 오른쪽 구성원 리스트-->
      <v-col cols="8">
        <v-sheet
            class="d-flex flex-wrap">
          <v-card
              width="100%"
              class="mb-5 ml-5"
              min-width="100"
              max-width="240"
              rounded="xl"
              elevation="5"
          >
            <v-img
                class="align-end text-white"
                height="200"
                :src="`/images/profile/user${Math.ceil((Math.random() * 14))}.jpg`"
                cover
            >
            </v-img>
            <v-card-title v-text="`❤️ ${groupUser.getGroupName}`"/>
            <v-card-subtitle v-text="groupUser.getGroupType === 'DEPARTMENT' ? '🏢 부서조직': '🚀 NewBiz팀' "/>

            <v-card-actions class="d-flex justify-center">
              <!--              신규 팀원 추가 버튼-->
              <v-btn
                  @click="setAllUserInfoInKip"
                  variant="elevated"
                  color="green-lighten-1"
                  class="ma-2 px-4"
                  text="신규팀원추가"/>
            </v-card-actions>
          </v-card>

          <!--           그룹에 소속된 회원 리스트-->
          <v-card
              width="100%"
              v-for="user in groupUser.getUsersInfoInGroup"
              :key="user.userId"
              class="mb-5 ml-5"
              min-width="100"
              max-width="240"
              rounded="xl"
              elevation="5"
          >
            <v-img
                class="align-end text-white"
                height="200"
                :src="user.profileImageUrl"
                cover
            >
            </v-img>
            <v-card-title v-text="`${user.groupRole === 'SUPER'? '👑' : '✅'} ${user.name} `"/>
            <v-card-subtitle v-text="`📞 ${user.phoneNumber}`"/>


            <v-card-actions class="d-flex justify-center">
              <v-btn
                  @click="groupUser.updateUserRoleInGroup(clickedGroupId, user.userId)"
                  variant="elevated"
                  :color="color.kipMainColor"
                  class="ma-2 px-3"
                  text="역할변경"/>
              <v-btn
                  @Click="deleteUserFromGroup(clickedGroupId, user.userId)"
                  variant="elevated"
                  color="red-lighten-1"
                  class="ma-2 px-3"
                  text="그룹제외"/>
            </v-card-actions>
          </v-card>
        </v-sheet>
      </v-col>
    </v-row>
  </v-container>
</template>
<style>

</style>