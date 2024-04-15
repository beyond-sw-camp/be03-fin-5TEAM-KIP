<script setup>
import {VTreeview} from 'vuetify/labs/VTreeview'

// 피니아
const group = useGroup();
const color = useColor();
const groupuser = useGroupuser();

// 데이터 세팅
await group.setHierarchyInfo();
const groups = group.getHierarchyInfo;
const open = ref();
const clickedGroupId = ref(1);

const setUsersInfoInGroup = async (groupId) => {
  console.log(groupId,"그룹아이디")
  clickedGroupId.value = groupId
  await groupuser.setUsersInfoInGroup(groupId);
}
</script>

<template>
  <v-container fluid>
    <v-row justify="center">
      <v-col cols="4">
        <!--          왼쪽 조직 리스트-->
        {{clickedGroupId}} {{groupuser.getGroupName}}
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
      <v-col cols="8">
        <!--        오른쪽 구성원 리스트-->
        <v-sheet
            class="d-flex flex-wrap">

          <v-card
              width="100%"
              v-for="user in groupuser.getUsersInfoInGroup"
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
              <v-btn variant="elevated" :color="color.kipMainColor" class="ma-2" text="역할변경"></v-btn>
              <v-btn variant="elevated" color="red-lighten-1" class="ma-2" text="삭제"></v-btn>
            </v-card-actions>
          </v-card>
        </v-sheet>
      </v-col>
    </v-row>
  </v-container>
</template>
<style>

</style>