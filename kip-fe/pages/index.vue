<script lang="ts" setup>

import type {BreadcrumbLink} from '#ui/types'
import {safelist} from "#tailwind-config";

const links = [{
  label: 'Home',
  icon: 'i-heroicons-home',
  to: '/'
}, {
  label: 'Navigation',
  icon: 'i-heroicons-square-3-stack-3d'
}, {
  label: 'Breadcrumb',
  icon: 'i-heroicons-link'
}]

export interface Props {
  links: BreadcrumbLink[]
}

defineProps<Props>()

const colorMode = useColorMode()
const isDark = computed({
  get() {
    return colorMode.value === 'dark'
  },
  set() {
    colorMode.preference = colorMode.value === 'dark' ? 'light' : 'dark'
  }
})
</script>

<template>
  <div>
    Page: Welcome To KIP (knowledge is Power)
  </div>

  <UBreadcrumb :links="links">
    <template #icon="{ link }">
      <UIcon :name="link.icon"/>
    </template>
  </UBreadcrumb>

  <UButton>
    Primary Color
  </UButton>
  <UFormGroup name="email" label="Email" :ui="{ label: { base: 'font-semibold' } }"/>
  <UContainer :ui="{ constrained: 'max-w-2xl' }">
    <slot/>
  </UContainer>
  <ClientOnly>
    <UButton
        :icon="isDark ? 'i-heroicons-moon-20-solid' : 'i-heroicons-sun-20-solid'"
        color="gray"
        variant="ghost"
        aria-label="Theme"
        @click="isDark = !isDark"
    />

  </ClientOnly>
  <UButton icon="i-heroicons-arrow-down">안녕하세요</UButton>
</template>

<style scoped></style>
