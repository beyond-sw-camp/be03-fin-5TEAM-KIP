<script setup>
import {ref} from "vue";
import {toastViewerInstance} from "~/useToastViewer";

const props = defineProps({
  form : []
})
const versions = useVersion()
const versionContent = ref();
onMounted(async () => {
  await versions.setVersionDetail(props.form.versionId)
  versionContent.value = versions.getDetail
  viewer.value = toastViewerInstance(
      viewer.value,
      versionContent.value
  );
})
const viewer = ref();
</script>

<template>
<<<<<<< Updated upstream
  <div class="mb-4 pt-4 pr-4 pl-4">
    <div class="headline">📬 수정내역 : {{ props.form.message }}</div>
    <div class="d-flex justify-lg-space-between mt-5">
      <div class="pl-4"> ✏️ 작성자 : {{ props.form.writer }}</div>
      <span>⌚ 수정일시 : {{ props.form.createdTime }}</span>
    </div>
  </div>
  <v-card
      class="viewer__scroll pa-4 rounded-xl"
  elevation="5">
    <div ref="viewer" class="viewer__scroll pa-4"></div>
  </v-card>
</template>

<style scoped>
.viewer__scroll {
  overflow-y: auto;
}

.headline {
  font-size: 1.5rem;
  font-weight: bold;
}
</style>