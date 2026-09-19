<template>
  <div class="min-h-screen bg-ink flex flex-col">
    <PublicHeader />
    <main class="flex-grow pt-16">
      <slot />
    </main>
    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import PublicHeader from '@/components/PublicHeader.vue'
import PublicFooter from '@/components/PublicFooter.vue'
</script>