<script setup lang="ts">
const route = useRoute()
const groupId = route.params.groupId;
const message = computed( () =>
    `이 그룹의 그룹 아이디는 ${groupId} 입니다. `
)
</script>

<template>
{{message}}
</template>

<style scoped>

</style>