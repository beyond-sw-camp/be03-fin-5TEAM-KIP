<script setup>
import {VTreeview} from 'vuetify/labs/VTreeview'

// 피니아
const user = useUser();
const group = useGroup();
const color = useColor();
const groupUser = useGroupuser();


const loading = ref(false);
const open = ref();
const clickedGroupId = ref(1);
const addNewMemberModdal = ref();
const createMemberModdal = ref();
const employedDay = ref()

// 상단 네비 제목 설정
group.TopNaviGroupList = ["Knowledge is Power", "부서목록", "타 부서 문서와 구성원을 조회할 수 있습니다. 🥩️"];

// 데이터 세팅
await group.setHierarchyInfo();
const groups = group.getHierarchyInfo;

// 신규계정 데이터 관련
const showPassword = ref(false)
const showPasswordConfirm = ref(false);
const passwordConfirm = ref('');


// 그릅 유저 정보 초기화
groupUser.$reset();
await groupUser.setUsersInfoInGroup(clickedGroupId.value);


// ❤️ 유저들의 정보를 세팅하는 함수들
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


// ⏩ 회원가입 관련 세팅

// 전송할 데이터
const data = ref({
  name: '',
  email: '',
  phoneNumber: '',
  password: '',
  employedDay: '',
  employeeId: '',
});

// 최종 제출 관련 함수
const handleSubmit = async (event) => {
  loading.value = true
  const results = await event
  await wait(500); // 1.2초 대기
  loading.value = false

  if (results.valid) {
    await user.createUserAccount(data.value)
    await groupUser.addCreatedUserToAllUsers(user.getCreatedUserData)
    alert(`${user.getCreatedUserData.name}님의 계정이 생성되었습니다.`)
    Object.keys(data.value).forEach(key => data.value[key] = "");
    passwordConfirm.value = ""
    createMemberModdal.value = false; // 모달창 닫기
  }

}

// 한국말로 입사일 형식 변경하는 함수
const formattedDate = () => {
  let date = new Date(employedDay.value);
  let options = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }
  let formattedDate = date.toLocaleDateString('ko-KR', options);
  data.value.employedDay = formattedDate
  return formattedDate
}




// 폼데이터 벨리데이션 체크
const rules = {
  nameRule: value => !!value || '이름 입력이 필요합니다.',

  // 비밀번호
  passwordRule: value => /^\d{4}$/.test(value) || '숫자 4자리로 입력해주세요.',
  passwordConfim: value => data.value.password === value || '비밀번호가 일치하지 않습니다.',

  // 사번
  employeeIdRule: value => /^k-\d{10}$/.test(value) || '사번은 k- 포함 숫자 12자리 입니다',
  employeeIdDupulicateCheck: async value => {
    await user.isExistEmployeeIdForCreate(value)
    return !user.getIsExistIdForCreate || '이미 존재하는 사번입니다.'
  },

  // 연락처
  phoneNumberRule: value => /^010-\d{4}-\d{4}$/.test(value) || '010- 으로 시작하는 8자리 숫자를 입력해주세요',
  phoneNumberDupulicateCheck: async value => {
    await user.isExistPhoneNumberForCreate(value)
    return !user.getIsExistPhoneNumber || '연락처가 중복됩니다.'
  },

  // 이메일
  emailRule: value => {
    const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    return pattern.test(value) || '이메일 형식이 아닙니다.'
  },
  emailDupulicateCheck: async value => {
    await user.isExistEmailForCreate(value)
    return !user.getIsExistEmail || '같은 이메일이 존재합니다.'
  },

  // 입사일
  employedDayRule: value => /^(19\d{2}|20\d{2})년 (\d{1,2})월 (\d{1,2})일 [월화수목금토일]요일$/.test(value) || '오른쪽 달력을 클릭하여 입력해 주세요',
};
// ⏩ 회원가입 관련 세팅 끝
</script>

<template>
  <v-container fluid>

    <!--      ✅ 그룹에 사원 추가를 위한 다이얼로그 -->
    <v-dialog
        width="80vw"
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
    </v-dialog>


    <!--   🥩 신규 회원 생성을 위한 다이얼로그 -->
    <v-dialog
        class="d-flex justify-center"
        width="70vw"
        height="90vh"
        opacity="50%"
        v-model="createMemberModdal">
      <v-sheet
          rounded="xl"
          class="d-flex justify-center flex-wrap pa-10">

        <!--           ❤️ 그룹에 소속된 회원 리스트-->
        <v-form ref="form" style="width: 75vw" @submit.prevent="handleSubmit">
          <v-row>
            <v-col>
              <v-text-field
                  label="사번"
                  placeholder="k-1234567890"
                  v-model="data.employeeId"
                  :rules="[rules.employeeIdRule, rules.employeeIdDupulicateCheck]"
                  clearable
                  maxlength="12"
                  required
                  counter
              />
              <v-text-field
                  label="비밀번호"
                  placeholder="1234"
                  v-model="data.password"
                  :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                  :rules="[rules.passwordRule]"
                  :type="showPassword ? 'text' : 'password'"
                  hint="4자리 숫자"
                  maxlength="4"
                  name="input-10-1"
                  clearable
                  counter
                  @click:append="showPassword = !showPassword"
              />
              <v-text-field
                  label="비밀번호 확인"
                  placeholder="1234"
                  v-model="passwordConfirm"
                  :append-icon="showPasswordConfirm ? 'mdi-eye' : 'mdi-eye-off'"
                  :rules="[rules.passwordRule, rules.passwordConfim]"
                  :type="showPasswordConfirm ? 'text' : 'password'"
                  maxlength="4"
                  hint="4자리 숫자"
                  name="input-10-1"
                  clearable
                  counter
                  @click:append="showPasswordConfirm = !showPasswordConfirm"
              />
              <v-text-field
                  label="이름"
                  placeholder="홍길동"
                  v-model="data.name"
                  :rules="[rules.nameRule]"
                  clearable
                  required
              />
              <v-text-field
                  label="연락처"
                  placeholder="010-1234-5678"
                  v-model="data.phoneNumber"
                  :rules="[rules.phoneNumberRule, rules.phoneNumberDupulicateCheck]"
                  clearable
                  maxlength="13"
                  required
                  counter
              />
              <v-text-field
                  label="이메일"
                  placeholder="admin@kip.com"
                  v-model="data.email"
                  :rules="[rules.emailRule, rules.emailDupulicateCheck]"
                  clearable
                  required
              />
              <v-text-field
                  v-model="data.employedDay"
                  label="입사일"
                  :rules="[rules.employedDayRule]"
                  required
                  clearable
              />
            </v-col>
            <v-col>
              <v-date-picker
                  v-model="employedDay"
                  width="100%"
                  @click="formattedDate"
                  show-adjacent-months
              />
              <v-btn
                  class="mt-4"
                  color="success"
                  :loading="loading"
                  text="신규 계정 생성하기"
                  type="submit"
                  block
              />
            </v-col>
          </v-row>
        </v-form>
      </v-sheet>
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