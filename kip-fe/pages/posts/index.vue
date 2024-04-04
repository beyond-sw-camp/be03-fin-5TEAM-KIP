<script setup lang="ts">
const search = ref()
const selected = ref()
const headers = ref([
  {
    align: 'start',
    key: 'author',
    sortable: false,
    title: 'author',
  },
  {key: 'title', title: 'title'},
])
const posts = ref([
  {title: "Post 1", author: "Fred"},
  {title: "Post 2", author: "sejong"},
  {title: "Post 3", author: "sejong"},
  {title: "Post 4", author: "Fred"},
  {title: "Post 5", author: "Fred"},
  {title: "Post 6", author: "Fred"},
  {title: "Post 7", author: "Fred"},
  {title: "Post 8", author: "Fred"},
  {title: "Post 9", author: "Fred"},
  {title: "Post 10", author: "Fred"},
  {title: "Post 11", author: "Fred"},
  {title: "Post 12", author: "Fred"},
  {title: "Post 13", author: "Fred"},
  {title: "Post 14", author: "Fred"},
  {title: "Post 15", author: "Fred"},
  {title: "Post 16", author: "Fred"},
  {title: "Post 17", author: "Fred"},
  {title: "Post 18", author: "Fred"},
  {title: "Post 19", author: "Fred"},
  {title: "Post 20", author: "Fred"},
  {title: "Post 21", author: "Fred"},
  {title: "Post 22", author: "Fred"},
]);
</script>

<template>
  {{ selected }}
  <v-card
      title="Nutrition"
      flat
  >
    <template v-slot:text>
      <v-text-field
          v-model="search"
          label="Search"
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          hide-details
          single-line
      ></v-text-field>
    </template>

    <v-data-table
        :headers="headers"
        :items="posts"
        :search="search"
        show-select
        item-value="title"
        v-model="selected"
    >


      <template #item.title="{ item }">
        <v-dialog fullscreen>
          <template v-slot:activator="{ props: activatorProps }">
            <span
                v-bind="activatorProps"
                class="cursor-pointer"
            >{{ item.title }}</span>
          </template>

          <template v-slot:default="{ isActive }">
            <v-card title="Dialog">
              <v-card-text>
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
                dolore magna aliqua.
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>

                <v-btn
                    text="Close Dialog"
                    @click="isActive.value = false"
                ></v-btn>
              </v-card-actions>
            </v-card>
          </template>
        </v-dialog>
      </template>


    </v-data-table>
  </v-card>

</template>

<style scoped>

</style>