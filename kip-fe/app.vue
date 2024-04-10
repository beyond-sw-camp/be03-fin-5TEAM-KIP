<script setup>
import ProductStore from "~/stores/ProductStore.js";
import CartStore from "~/stores/CartStore.js";
import AuthUserStore from "~/stores/AuthUserStore.js";

ProductStore().fill();
CartStore().fetchItemsFromLocalStorage(); // 로컬스토리지에서 가지고옴.
AuthUserStore().setUserInfoAndTokenToPiniaFromLocalStorage();
</script>

<template>
  <NuxtLoadingIndicator />
  <NuxtLayout>
    <NuxtPage/>
  </NuxtLayout>
</template>

<!-- 전역 스타일 -->
<style>
::-webkit-scrollbar {
  display: none; /* Chrome, Safari */
}

a {
  color: inherit;
  text-decoration: none;
}

</style>

<!-- v-container 프론트 반응형 1 단위 -->
<!-- xl 1920(160px) => 1800(150px) -->
<!-- lg 1280(106px) => 1200(100px) -->
<!-- md  960( 80px) =>  900( 75px) -->
<!-- sm  600( 50px) =>  959( 80px) -->

<!--HD  1280× 720-->
<!--FHD 1920×1080-->
<!--QHD 2560×1440-->
<!--UHD 3840×2160-->