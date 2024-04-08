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
  <v-divider></v-divider>

  <v-list density="compact" nav>
    <NuxtLink to="/">
      <v-list-item prepend-icon="mdi-home-circle" title="HOME" value="myfiles"/>
    </NuxtLink>
    <NuxtLink to="/posts">
      <v-list-item prepend-icon="mdi-note" title="POSTING" value="shared"/>
    </NuxtLink>
    <NuxtLink to="/comments">
      <v-list-item prepend-icon="mdi-comment" title="COMMENTS" value="COMMENTS"/>
    </NuxtLink>
    <NuxtLink to="/login">
      <v-list-item prepend-icon="mdi-login-variant" title="LOGIN" value="LOGIN"/>
    </NuxtLink>
    <NuxtLink to="/pinia">
      <v-list-item prepend-icon="mdi-store-cog" title="PINIA" value="PINIA"/>
    </NuxtLink>
  </v-list>
</template>
