<script setup lang="ts">
import {ref} from "vue";

const props = defineProps<{
  post: Record<string, any>;
}>();
const form = ref({
  title: "",
  tags: [],
  published: null,
  body: "",
  image: [],
  ...props.post,
});

function handleSubmit() {
  // 수정내용 DB 저장
  console.log("submitting", form.value);
}

const submitBtn = ref();
const submit = () => submitBtn.value.click();
defineExpose({submit});
</script>


<template>
  <v-form @submit.prevent="handleSubmit" ref="formEl">
    <v-row>
      <v-col cols="3">
        <v-switch label="Published" v-model="form.published"></v-switch>
      </v-col>
      <v-col cols="3">
        <v-checkbox
            indeterminate
            v-model="form.published"
            label="Published"
        ></v-checkbox>
      </v-col>
    </v-row>

    <v-text-field
        v-model="form.title"
        label="Title"
    />
    <v-combobox
        v-model="form.tags"
        :items="['News', 'Tutorial', 'Event']"
        label="Tags"
        multiple
        chips
    ></v-combobox>
    <v-file-input
        accept="image/png, image/jpeg, image/bmp"
        placeholder="Uplaod a Feature Image"
        prepend-icon="mdi-camera"
        label="Feature Image"
        v-model="form.image"
    ></v-file-input>
    <v-textarea label="Post Body" v-model="form.body"></v-textarea>

    <button ref="submitBtn" type="submit" class="d-none">Submit</button>
  </v-form>
</template>