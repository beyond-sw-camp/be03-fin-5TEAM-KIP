<script setup>
await useUser().setUserInfoAndTokenToPiniaFromLocalStorage();

// 파비콘
useHead({
  titleTemplate: (titleChunk) => {
    return titleChunk ? `${titleChunk} - Knowledge Is Power` : 'Knowledge Is Power (KIP)'
  }
})
</script>

<template>
  <NuxtLoadingIndicator />
  <NuxtLayout>
    <NuxtPage/>
  </NuxtLayout>
</template>


<!-- v-container 프론트 반응형 1 단위 -->
<!-- xl 1920(160px) => 1800(150px) -->
<!-- lg 1280(106px) => 1200(100px) -->
<!-- md  960( 80px) =>  900( 75px) -->
<!-- sm  600( 50px) =>  959( 80px) -->

<!--UHD 3840×2160-->
<!--QHD 2560×1440-->
<!--FHD 1920×1080-->
<!--HD  1280× 720-->
