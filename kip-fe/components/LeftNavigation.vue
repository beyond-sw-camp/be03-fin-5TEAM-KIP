<script setup>
const rail = ref(true);
const emit = defineEmits(["railEvent"]);

// function
const handleRailClick = () => {
  emit('railEvent');
  rail.value = !rail.value;
};
</script>

<template>
    <v-sheet class="left__nav__sheet">
      <v-btn
          v-if="rail"
          icon="mdi-chevron-right"
          variant="text"
          @click="handleRailClick"
      />
      <v-btn
          v-else
          icon="mdi-chevron-left"
          variant="text"
          @click="handleRailClick"
      />
      <v-list density="compact">
        <NuxtLink to="/posts">
          <v-list-item prepend-icon="mdi-note" title="POSTING" value="shared" class="left__nav__list"/>
        </NuxtLink>
        <NuxtLink to="/comments">
          <v-list-item prepend-icon="mdi-comment" title="COMMENTS" value="COMMENTS" class="left__nav__list"/>
        </NuxtLink>
        <NuxtLink to="/kip">
          <v-list-item prepend-icon="mdi-store-cog" title="KIP" value="kip" class="left__nav__list"/>
        </NuxtLink>
        <NuxtLink to="/jiyongMainpractice">
          <v-list-item prepend-icon="mdi-arm-flex" title="JiYoung2" value="JiYoung2" class="left__nav__list"/>
        </NuxtLink>
        <NuxtLink to="/practice">
          <v-list-item prepend-icon="mdi-arm-flex" title="JiYoung3" value="JiYoung3" class="left__nav__list"/>
        </NuxtLink>
        <NuxtLink to="/mypage_test">
          <v-list-item prepend-icon="mdi-arm-flex" title="JiYoung3" value="JiYoung3" class="left__nav__list"/>
        </NuxtLink>
      </v-list>
    </v-sheet>
</template>git

<style>
.left__nav__sheet {
color: var(--primary-color);
}

.left__nav__list {
  padding-left: 10px !important;
}
</style>