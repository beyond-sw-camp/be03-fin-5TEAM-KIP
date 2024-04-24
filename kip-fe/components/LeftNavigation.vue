<script setup>
// 왼쪽 네비게이션 오른쪽으로 확장 축소 하는 코드.

const rail = ref(true);
const emit = defineEmits(["railEvent"]);
const handleRailClick = () => {
  emit('railEvent');
  rail.value = !rail.value;
};

// 피니아
const group = useGroup();
const color = useColor();

const documentList = useDocumentList();


await group.$reset(); // 그룹정보 리셋 후 다시 세팅
await group.setMyGroupsInfo();  // (awit) 그룹정보를 모두 가지고 온뒤 넘어감

await documentList.$reset();
await documentList.setFirstDocumentDetails();

</script>

<template>
  <v-sheet class="left__nav__sheet">
    <v-btn
        v-if="rail"
        icon="mdi-chevron-right"
        variant="text"
        @click="handleRailClick"
        class="group__list"
    />
    <v-btn
        v-else
        icon="mdi-chevron-left"
        variant="text"
        @click="handleRailClick"
        class="group__list"
    />

    <v-list density="comfortable">
      <v-list-item
          title="전체공개문서"
          value="publicOpenDoc"
          to="/publicOpenDoc"
          prepend-icon="mdi-web"
          :color="color.kipMainColor"
          rounded="xl"
          variant="text"
          class="group__list"/>

<!--      <v-list-item-->
<!--          title="문서목록테스트"-->
<!--          value="publicDoc"-->
<!--          to="/publicDoc"-->
<!--          prepend-icon="mdi-web"-->
<!--          :color="color.kipMainColor"-->
<!--          rounded="xl"-->
<!--          variant="text"-->
<!--          class="group__list"/>-->

      <v-list-item
          title="부서목록"
          value="groupList"
          to="/groupList"
          prepend-icon="mdi-account-group"
          :color="color.kipMainColor"
          rounded="xl"
          variant="text"
          class="group__list"/>

<!--      <v-list-item-->
<!--          to="/kip"-->
<!--          title="KIP"-->
<!--          value="KIP"-->
<!--          prepend-icon="mdi-store-cog"-->
<!--          :color="color.kipMainColor"-->
<!--          rounded="xl"-->
<!--          variant="text"-->
<!--          class="group__list"/>-->

<!--      <v-list-item-->
<!--          title="main-test"-->
<!--          value="main-test"-->
<!--          to="/main-test"-->
<!--          prepend-icon="mdi-arm-flex"-->
<!--          :color="color.kipMainColor"-->
<!--          rounded="xl"-->
<!--          variant="text"-->
<!--          class="group__list"/>-->

      <v-list-item
          title="bookmarks"
          value="bookmarks"
          to="/bookmarks"
          prepend-icon="mdi-list-box-outline"
          :color="color.kipMainColor"
          rounded="xl"
          variant="text"
          class="group__list"/>

      <v-divider class="group__list"/>

      <!-- 부서목록 -->
      <v-list-item
          v-for="item in group.getMyGroupNamesAndId" :key="item.groupId"
          :to="`/group/${item.groupId}`"
          :prepend-icon="item.groupType === 'DEPARTMENT'
          ? 'mdi-file-account-outline' : 'mdi-alpha-b-box-outline'"
          :title="`${item.groupName} ${item.groupType === 'DEPARTMENT' ? '&nbsp 🏢' : '&nbsp 🚀'}`"
          :value="item.groupName"
          :color="color.kipMainColor"
          rounded="xl"
          variant="text"
          class="group__list"/>

    </v-list>
  </v-sheet>
</template>

<style>
.left__nav__sheet {
  color: var(--primary-color);
}
.group__list{
  margin-top: 5px;
}
.v-list-item__spacer {
  width: 13px !important;
}
</style>