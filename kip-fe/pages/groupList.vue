<script setup>
import {VTreeview} from 'vuetify/labs/VTreeview'

// 피니아
const group = useGroup();
const color = useColor();
const groupUser = useGroupuser();

// 데이터 세팅
await group.setHierarchyInfo();
const groups = group.getHierarchyInfo;
const open = ref();
const clickedGroupId = ref(1);
const dialog = ref();

// 그릅 유저 정보 초기화
groupUser.$reset();
await groupUser.setUsersInfoInGroup(clickedGroupId.value);

const setUsersInfoInGroup = async (groupId) => {
  clickedGroupId.value = groupId
  await groupUser.setUsersInfoInGroup(groupId);
}
</script>

<template>
  <v-container fluid>
    <v-row justify="center">
      <v-col cols="4">

<!--        신규 회원 추가를 위한 다아일로그 -->
        <v-dialog
            v-model="dialog"
            width="auto"
        >
          <v-card
              max-width="400"
              prepend-icon="mdi-update"
              text="회원 전체 리스트"
              title="회원 전체 리스트"
          >
            <template v-slot:actions>
              <v-btn
                  class="ms-auto"
                  text="Ok"
                  @click="dialog = false"
              ></v-btn>
            </template>
          </v-card>
        </v-dialog>

        <!--          왼쪽 조직 리스트-->
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

      <!--        오른쪽 구성원 리스트-->
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

              <v-btn
                  @click="dialog = true"
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
                  :color="color.kipSubColor"
                  class="ma-2 px-3"
                  text="역할변경"/>
              <v-btn
                  @Click="groupUser.deleteUserFromGroup(clickedGroupId, user.userId)"
                  variant="elevated"
                  :color="color.kipMainColor"
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