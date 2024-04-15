<script setup>
import { VTreeview } from 'vuetify/labs/VTreeview'

// 피니아
const group = useGroup();
const color = useColor();

// 데이터 세팅
await group.setHierarchyInfo();
const groups = group.getHierarchyInfo;
const open = ref([1, 2]);
const clickedGroupId = ref(1);

const setGroupUsersInfo = (groupId) => {
  clickedGroupId.value = groupId
  group.setGroupUsersInfo(groupId);
}

</script>

<template>
  <v-card
      class="mx-auto"
      max-width="400"
  >
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
              ? 'account-group' : 'folder-network'}`"
              @click="setGroupUsersInfo( item.id)"
          ></v-icon>
        </template>
        <template v-slot:title="{ item }">
          <div @click="setGroupUsersInfo( item.id)">{{item.title}}
          </div>
        </template>
      </v-treeview>
    </v-card-text>
  </v-card>
</template>

<style>

</style>