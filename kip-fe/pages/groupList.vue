<script setup>
import { VTreeview } from 'vuetify/labs/VTreeview'
import {id} from "vuetify/locale";

const open = ref([1, 2]);
const items = ref([
  {
    id: 1,
    title: 'Vuetify Human Resources',
    children: [
      {
        id: 205,
        title: 'Andrew',
      },

        {
        id: 2,
        title: 'Core team',
        children: [
          {
            id: 201,
            title: 'John',
            children: [
              {
                id: 201,
                title: 'John',
              },
              {
                id: 202,
                title: 'Kael',
              },
              {
                id: 203,
                title: 'Nekosaur',
              },
              {
                id: 204,
                title: 'Jacek',
              },
              {
                id: 205,
                title: 'Andrew',
              },
            ],
          },
          {
            id: 202,
            title: 'Kael',
          },
          {
            id: 203,
            title: 'Nekosaur',
          },
          {
            id: 204,
            title: 'Jacek',
          },
          {
            id: 205,
            title: 'Andrew',
          },
        ],
      },
      {
        id: 3,
        title: 'Administrators',
        children: [
          {
            id: 301,
            title: 'Mike',
          },
          {
            id: 302,
            title: 'Hunt',
          },
        ],
      },
      {
        id: 4,
        title: 'Contributors',
        children: [
          {
            id: 401,
            title: 'Phlow',
          },
          {
            id: 402,
            title: 'Brandon',
          },
          {
            id: 403,
            title: 'Sean',
          },
        ],
      },
    ],
  },
])
const group = useGroup();
await group.setHierarchyInfo();
const groups = group.getHierarchyInfo;

const clicked = ref("hi");
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
      >
        <template v-slot:prepend="{ item }">
          <v-icon
              v-if="item.children"
              :icon="`mdi-${item.children.length === 0
              ? 'account-group' : 'folder-network'}`"
              @click="clicked = item.id"
          ></v-icon>
        </template>
        <template v-slot:title="{ item }">
          <div @click="clicked = item.id">{{item.title}}
          </div>
        </template>
      </v-treeview>
    </v-card-text>
  </v-card>
  {{clicked}}
</template>

<style scoped>

</style>