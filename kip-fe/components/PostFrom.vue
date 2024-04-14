<script setup lang="ts">
const emit = defineEmits(["submit"]);
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


const valid = ref(true);

function isNotEmpty(value: string[]) {
  if (value && value.length) return true;
  return "Please choose at least one option";
}

function isRequired(value: string) {
  if (value) return true;
  return "Field is required";
}

async function asyncValidtion(value: string) {
  const res = await fetch(`https://httpbin.org/status/${value}`);
  if (res.ok) return true;
  return "Bad response code";
}

function handleSubmit() {
  if (!valid.value) return;
  emit("submit");
}


const submitBtn = ref();
const submit = () => submitBtn.value.click();
defineExpose({submit});

</script>


<template>


  <!--폼 안에 속한 :rules 울모두 만족시키면 valid 가 작동한다. -->
  <v-form
      v-model="valid"
      @submit.prevent="handleSubmit"
      ref="formEl">

    <!-- 체크박스 -->
    <v-row>
      <v-col cols="3">
        <v-switch
            label="Published"
            v-model="form.published"/>
      </v-col>
      <v-col cols="3">
        <v-checkbox
            indeterminate
            v-model="form.published"
            label="Published"/>
      </v-col>
    </v-row>

    <!-- 제목 -->
    <v-text-field
        v-model="form.title"
        label="Title"
        :rules="[isRequired]"/>

    <!-- 태그 -->
    <v-combobox
        v-model="form.tags"
        :items="['News', 'Tutorial', 'Event']"
        label="Tags"
        multiple
        :rules="[isNotEmpty]"
        chips/>

    <!-- 파일첨부 -->
    <v-file-input
        accept="image/png, image/jpeg, image/bmp"
        placeholder="Uplaod a Feature Image"
        prepend-icon="mdi-camera"
        label="Feature Image"
        v-model="form.image"/>

    <!-- 내용 -->
    <v-textarea label="Post Body" v-model="form.body"/>
    Form is Valid? {{ valid }}
    <button ref="submitBtn" type="submit" class="d-none">Submit</button>
  </v-form>
  <v-alert
      v-if="valid"
      v-model="valid"
      type="success"
      text="Can Submit"
      closable
      dismissible
      variant="flat"/>
  <v-alert
      v-if="!valid"
      type="error"
      text="Can't Submit"
      closable
      dismissible
      variant="flat"/>

</template>