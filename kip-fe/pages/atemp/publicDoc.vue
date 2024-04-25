<script setup>
const group = useGroup();
// 상단 네비 제목 설정
group.TopNaviGroupList = ["Knowledge is Power","전체공개문서","해시태그로 검색해 주세요.🏷️"];


const postForm = ref();
const search = ref()
const selected = ref()
const headers = ref([
  {key: 'title', title: 'Title (Click To Edit)'},
  {
    align: 'start',
    key: 'author',
    sortable: false,
    title: 'author',
  },
])
const posts = ref([
  {title: "(Click To Edit) Post 1", author: "Fred"},
  {title: "(Click To Edit) Post 2", author: "sejong"},
  {title: "(Click To Edit) Post 3", author: "sejong"},
  {title: "(Click To Edit) Post 4", author: "Fred"},
  {title: "(Click To Edit) Post 5", author: "Fred"},
  {title: "(Click To Edit) Post 6", author: "Fred"},
  {title: "(Click To Edit) Post 7", author: "Fred"},
  {title: "(Click To Edit) Post 8", author: "Fred"},
  {title: "(Click To Edit) Post 9", author: "Fred"},
  {title: "(Click To Edit) Post 10", author: "Fred"},
  {title: "(Click To Edit) Post 11", author: "Fred"},
  {title: "(Click To Edit) Post 12", author: "Fred"},
  {title: "(Click To Edit) Post 13", author: "Fred"},
  {title: "(Click To Edit) Post 14", author: "Fred"},
  {title: "(Click To Edit) Post 15", author: "Fred"},
  {title: "(Click To Edit) Post 16", author: "Fred"},
  {title: "(Click To Edit) Post 17", author: "Fred"},
  {title: "(Click To Edit) Post 18", author: "Fred"},
  {title: "(Click To Edit) Post 19", author: "Fred"},
  {title: "(Click To Edit) Post 20", author: "Fred"},
  {title: "(Click To Edit) Post 21", author: "Fred"},
  {title: "(Click To Edit) Post 22", author: "Fred"},
]);
</script>

<template>
  {{ selected }}
  <v-card
      title="Nutrition"
      flat
  >
    <template #text>
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

      <!--      클릭을 통해서 모달찰(dialong) 열기-->
      <template #item.title="{ item }">
        <v-dialog fullscreen>
          <template #activator="{ props: activatorProps }">
            <span
                v-bind="activatorProps"
                class="cursor-pointer"
            >{{ item.title }}</span>
          </template>

          <template v-slot:default="{ isActive }">
            <v-card title="Edit Post">
              <v-card-text>
                <PostFrom
                    ref="postForm"
                    :post="item"
                    @submit="isActive.value = false"/>
              </v-card-text>

              <v-card-actions>
                <v-spacer/>

                <v-btn
                    text="Cancel"
                    @click="isActive.value = false"/>

                <v-btn
                    color="blue"
                    variant="flat"
                    text="Save Post"
                    @Click="postForm.submit()"/>

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