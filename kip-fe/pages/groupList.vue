<script setup>
import { VTreeview } from 'vuetify/labs/VTreeview'

const open = ref([1, 2]);
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
</template>

<style>

</style>