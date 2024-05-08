<script setup>
const group = useGroup();
// 상단 네비 제목 설정
group.TopNaviGroupList = ["Knowledge is Power", "요청 내역 🔥"];

const requests = useRequest();
const nowTab = ref(1);
const myRequest = ref([]);
const receivedRequest = ref([]);
const dialog = ref(false);
const dialog_temp = ref();
const nowItem = ref();
const color = useColor();


onMounted(async () => {
  await requests.setMyRequest();
  await requests.setReceivedRequest();
  myRequest.value = requests.getMyRequest;
  receivedRequest.value = requests.getReceivedRequest;
});

const currentItems = computed(() => {
  return nowTab.value === 1 ? receivedRequest.value : myRequest.value;
});


const myHeaders = ref([
  {title: '문서 제목', value: 'documentName'},
  {title: '그룹 이름', value: 'groupName', align: 'center'},
  {title: '현재 상태', value: 'isOk', align: 'center'},
  {title: ' ', value: 'actions', align: 'end'},
  {title: '요청 일수', value: 'days', align: 'center'},
  {title: '유효 기간', value: 'dueDate', align: 'center'}
]);
const receivedHeaders = ref([
  {title: '문서 제목', value: 'documentName'},
  {title: '요청자', value: 'requesterName', align: 'center'},
  {title: '현재 상태', value: 'isOk', align: 'center'},
  {title: '', value: 'actions', align: 'end'},
  {title: '요청 일수', value: 'days', align: 'center'},
  {title: '유효 기간', value: 'dueDate', align: 'center'}
]);

const agreeRequest = (item) => {
  dialog_temp.value = 'AGREE';
  nowItem.value = item;
  dialog.value = true;
}
const rejectRequest = (item) => {
  dialog_temp.value = 'REJECT';
  nowItem.value = item;
  dialog.value = true;
}

const removeRequest = (item) => {
  dialog_temp.value = 'REMOVE';
  nowItem.value = item;
  dialog.value = true;
}
const confirmRejectRequest = async () => {
  await requests.rejectRequest(nowItem.value.requestId)
  await requests.setReceivedRequest();
  receivedRequest.value = requests.getReceivedRequest;
  dialog.value = false;
}
const confirmAgreeRequest = async () => {
  await requests.agreeRequest(nowItem.value.requestId)
  await requests.setReceivedRequest();
  receivedRequest.value = requests.getReceivedRequest;
  dialog.value = false;
}
const confirmRemoveMyRequest = async () => {
  await requests.removeMyRequest(nowItem.value.requestId)
  await requests.setMyRequest();
  myRequest.value = requests.getMyRequest;
  dialog.value = false;
}
const confirmRemoveReceivedRequest = async () => {
  await requests.removeReceivedRequest(nowItem.value.requestId)
  await requests.setReceivedRequest();
  receivedRequest.value = requests.getReceivedRequest;
  dialog.value = false;
}
</script>

<template>
  <v-container fluid>
    <!-- 상단 탭 네비게이션 -->
    <v-row>
      <v-col class="pa-6">
        <v-tabs centered
                v-model="nowTab">
          <v-tab
              class="px-16"
              style="font-size: 18px"
              :value="1"> 📫 받은 요청
          </v-tab>
          <v-tab
              class="px-16"
              style="font-size: 18px"
              :value="2"> ✉️ 보낸 요청
          </v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-card
            variant="text"
            class="px-8 mt-6"
            rounded="xl">
          <v-card-title class="text-h5 headline  mb-10"> 🔥 요청 내역 🔥</v-card-title>
          <v-data-table
              :headers="nowTab === 1 ? receivedHeaders : myHeaders"
              :items="currentItems"
          >
            <template v-slot:item.isOk="{ item }">
              <!-- 보낸 요청일 때 -->
              <template v-if="nowTab === 2">
                <v-icon
                    size="x-large"
                    v-if="item.isOk === 'Y'" color="green">
                  mdi-check
                </v-icon>
                <v-icon
                    size="xx-large"
                    v-else-if="item.isOk === 'P'">mdi-message-question-outline</v-icon>
                <v-icon
                    size="xx-large"
                    v-else color="red"> mdi-close</v-icon>
              </template>

              <!-- 받은 요청일 때 -->
              <template v-else-if="nowTab === 1">
                <!-- 'Y' 또는 'N'인 경우 아이콘 표시 -->
                <v-icon
                    size="x-large"
                    v-if="item.isOk === 'P'">mdi-minus</v-icon>
                <v-icon
                    size="x-large"
                    v-else-if="item.isOk === 'Y'" color="green">mdi-check</v-icon>
                <v-icon
                    size="x-large"
                    v-else color="red">mdi-close</v-icon>
              </template>
            </template>
            <template v-slot:item.dueDate="{ item }">
              <template v-if="item.dueDate === null">
                <v-icon>mdi-minus</v-icon>
              </template>
              <template v-else>
                {{ item.dueDate }}
              </template>
            </template>

            <template v-slot:item.actions="{ item }">
              <v-btn
                  v-if="item.isOk === 'P' && nowTab === 1"
                  variant="elevated"
                  rounded="xl"
                  @click="agreeRequest(item)"
                  :color="item.isShow==='N'? color.kipMainColor : 'green-darken-2'">
                <v-icon
                    color="white"
                    size="x-large"
                >
                  mdi-check
                </v-icon>
                <v-tooltip
                    activator="parent"
                    location="start">승인
                </v-tooltip>
              </v-btn>
              <v-btn
                  v-if="item.isOk === 'P' && nowTab === 1"
                  variant="elevated"
                  rounded="xl"
                  class="ml-2"
                  @click="rejectRequest(item)"
                  :color="item.isShow==='N'? color.kipMainColor : 'deep-orange-darken-4'">
                <v-icon
                    color="white"
                    size="x-large"
                >
                  mdi-close
                </v-icon>
                <v-tooltip
                    activator="parent"
                    location="end">거절
                </v-tooltip>
              </v-btn>

              <v-btn
                  v-if="item.isOk === 'Y' || item.isOk === 'N'"
                  variant="elevated"
                  rounded="xl"
                  class="mr-8"
                  @click="removeRequest(item)"
                  :color="item.isShow==='N'? color.kipMainColor : 'light-blue-darken-2'">
                <v-icon
                >
                  mdi-trash-can-outline
                </v-icon>
                <v-tooltip
                    activator="parent"
                    location="start">요청기록삭제
                </v-tooltip>
              </v-btn>
            </template>
            <template v-slot:no-data>
              요청이 존재하지 않습니다.
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
    <v-dialog
        v-model="dialog"
        width="30vw"
        opacity="10%"
    >
      <v-card
          rounded="xl"
          class="pa-6"
      >
        <v-card-title v-if="dialog_temp==='AGREE'" class="headline">요청 수락</v-card-title>
        <v-card-title v-else-if="dialog_temp==='REJECT'" class="headline">요청 거절</v-card-title>
        <v-card-title v-else class="headline">요청 삭제</v-card-title>
        <v-card-text v-if="dialog_temp==='AGREE'">정말 수락하시겠습니까?</v-card-text>
        <v-card-text v-else-if="dialog_temp==='REJECT'">정말 거절하시겠습니까?</v-card-text>
        <v-card-text v-else>정말 삭제하시겠습니까?</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn v-if="dialog_temp==='AGREE'"
                 variant="elevated"
                 :color="color.kipMainColor"
                 @click="confirmAgreeRequest">수락
          </v-btn>
          <v-btn v-else-if="dialog_temp==='REJECT'"
                 variant="elevated"
                 :color="color.kipMainColor"
                 @click="confirmRejectRequest">거절
          </v-btn>
          <v-btn v-else-if="nowTab===2" color="blue darken-1" @click="confirmRemoveMyRequest">삭제</v-btn>
          <v-btn v-else-if="nowTab===1" color="blue darken-1" @click="confirmRemoveReceivedRequest">삭제</v-btn>
          <v-btn color="blue darken-1" text @click="dialog = false">취소</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>


<style scoped>
.table-container {
  height: calc(100vh - 200px); /* 화면의 높이에서 200px을 제외한 높이로 설정 */
  overflow-y: auto; /* y축에 대해서만 스크롤 허용 */
}
</style>

