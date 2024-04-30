<script setup lang="ts">
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
  <div class="mb-4">
    <div class="font-weight-normal">
      <strong>{{ props.form.writer }}</strong> {{ props.form.createdTime }}
    </div>

    <div>{{ props.form.message }}</div>
  </div>
  <v-divider></v-divider>
  <v-card>
    <div ref="viewer"></div>
  </v-card>
</template>

<style scoped>
</style>