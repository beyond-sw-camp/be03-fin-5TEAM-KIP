<script setup>
const color = useColor();

const documentList = useDocumentList();
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
  {title: '미리보기', value: 'preview', align: 'center'},
  {title: '작성자', value: 'writer', align: 'center'},
  {title: '수정 일자', value: 'createdTime', align: 'center'},
  {title: '수정 메시지', value: 'message', align: 'center'},
  {title: '현재버전', value: 'isShow', align: 'center'},
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

const emit = defineEmits(["versionChanged"]);
const changeVersion = async () => {
  await versions.changeVersion(targetVersion.value)
  await versions.setVersions(props.selectDocumentId);
  versionList.value = versions.getVersions;
  await documentList.setDocumentDetails(documentList.getSelectedDocId);
  emit('versionChanged')
  dialog.value = false;
}

</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card outlined
                rounded="xl"
                class="pa-4">
          <v-card-title class="headline"> ✏️ 수정 이력</v-card-title>
          <v-data-table
              :headers="myHeaders"
              :items="versionList"
          >
            <template v-slot:item.preview="{ item }">
              <v-btn
                  variant="elevated"
                  rounded="xl"
                  @click="openVersionView(item)">
                <v-icon>mdi-eye</v-icon>
              </v-btn>
            </template>

            <template v-slot:item.isShow="{ item }">
              <v-btn
                  variant="elevated"
                  rounded="xl"
                  :color="item.isShow==='N'?color.kipMainColor:'green'"
                  @click="openVersionChange(item)">
                <v-icon
                    size="large"
                    v-if="item.isShow==='N'">
                  mdi-file-arrow-left-right-outline
                </v-icon>
                <v-icon v-else color="white"
                        size="xx-large"
                        icon="mdi-check"></v-icon>
                <v-tooltip
                    activator="parent"
                    location="start">버전변경
                </v-tooltip>
              </v-btn>
            </template>

            <template v-slot:no-data>
              수정이력 불러오는중
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
    <!-- 버전 미리보기 창 -->
    <v-dialog
        class="d-flex justify-cneter"
        width="60vw"
        opacity="80%"
        v-model="versionViewModal">
      <v-card
          rounded="xl"
          class="pa-8">

        <VersionView :form="selectedVersion"></VersionView>
      </v-card>
    </v-dialog>
  </v-container>

  <!--  버전 변경을 위한 모달-->
  <v-dialog
      width="40vw"
      opacity="10%"
      v-model="dialog">
    <v-card
        rounded="xl"
        class="pa-4">
      <v-card-title class="headline mt-4"> 🚀 버전 변경</v-card-title>
      <v-card-text>해당 버전으로 변경하시겠습니까?</v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
            variant="elevated"
            :color="color.kipMainColor" @click="changeVersion">변경</v-btn>
        <v-btn
            variant="elevated"
            color="red-darken-4" @click="dialog = false">취소</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<style>
.headline {
  font-size: 1.5rem;
  font-weight: bold;
}
.viewDetail{
  max-width: 800px;
  max-height:none;
}
</style>