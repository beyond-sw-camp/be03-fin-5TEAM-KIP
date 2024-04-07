<script setup>
import ProductStore from "~/stores/ProductStore.js";
import CartStore from "~/stores/CartStore.js";

ProductStore().fill();
CartStore().fetchItemsFromLocalStorage(); // 로컬스토리지에서 가지고옴.

</script>s

<template>
  <NuxtLoadingIndicator />
  <NuxtLayout>
  </NuxtLayout>
</template>

<!-- 전역 스타일 -->
<style>
a {
  color: inherit;
  text-decoration: none;
}
</style>