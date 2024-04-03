<script setup lang="ts">
const posts = ref([
  {title: "Post 1", author: "Fred"},
  {title: "Post 2", author: "Fred"},
  {title: "Post 3", author: "Fred"},
  {title: "Post 4", author: "Fred"},
  {title: "Post 5", author: "Fred"},
  {title: "Post 6", author: "Fred"},
  {title: "Post 7", author: "Fred"},
  {title: "Post 8", author: "Fred"},
  {title: "Post 9", author: "Fred"},
]);
</script>

<template>
  <v-table
  :height="300"
  :fixed-header="true"
  :density="'compact'"
  :hover="true"
  >
    <thead>
    <tr>
      <th class="text-left">
        Title
      </th>
      <th class="text-left">
        Author
      </th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="item in posts" :key="item.title">
      <td>{{ item.title }}</td>
      <td>{{ item.author }}</td>
    </tr>
    </tbody>
  </v-table>
</template>

<style scoped>

</style>