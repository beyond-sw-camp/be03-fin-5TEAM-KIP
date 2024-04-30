<script lang="ts" setup>

const versions = useVersion()
const props = defineProps({
  selectDocumentId: Number
    }
)
const versionViewModal = ref(false);
const dialog = ref(false)

const versionList = ref();
onMounted(async () => {
  await versions.setVersions(props.selectDocumentId);
  versionList.value = versions.getVersions;
});

const myHeaders = ref([
  { title: '작성자', value: 'writer', align: 'center'},
  { title: '수정 일자', value: 'createdTime', align: 'center'},
  { title: '수정 메시지', value: 'message', align: 'center'},
  { title: '현재 버젼', value: 'isShow', align: 'center'},
  { title: ' ', value : 'actions', align:'end'}
]);
const selectedVersion = ref();
const openVersionView = (item) => {
  selectedVersion.value = item
  versionViewModal.value = true
}
const targetVersion = ref();
const openVersionChange = (item) => {
  targetVersion.value = item.versionId;
  dialog.value = true
}
const changeVersion = async () => {
  await versions.changeVersion(targetVersion.value)
  await versions.setVersions(props.selectDocumentId);
  versionList.value = versions.getVersions;
  dialog.value = false;
}

</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card outlined class="pa-4">
          <v-card-title class="text-h5">수정 이력</v-card-title>
          <v-data-table
              :headers="myHeaders"
              :items="versionList"
          >
            <template v-slot:item.isShow="{ item }">
                <v-icon v-if="item.isShow === 'N'">mdi-minus</v-icon>
                <v-icon v-else color="green" icon="mdi-check"></v-icon>
            </template>
            <template v-slot:item.actions="{ item }">
              <v-icon v-if="item.isShow==='N'"
              @click="openVersionChange(item)">
                mdi-file-arrow-left-right-outline
              </v-icon>
              <v-icon
                  @click="openVersionView(item)"
              >
                mdi-eye
              </v-icon>
            </template>
            <template v-slot:no-data>
              글 작성 이력이 존재하지 않습니다.
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
    <v-dialog class="viewDetail" v-model="versionViewModal">
      <v-card>
        <VersionView :form="selectedVersion"></VersionView>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="versionViewModal=false">닫기</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
  <v-dialog v-model="dialog" persistent max-width="290">
    <v-card>
      <v-card-title class="text-h5">버전 변경</v-card-title>
      <v-card-text >해당 버전으로 변경하시겠습니까?</v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="dialog = false">취소</v-btn>
        <v-btn color="blue darken-1" @click="changeVersion">변경</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<style>
.viewDetail{
  max-width: 800px;
}
</style>