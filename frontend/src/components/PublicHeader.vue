<template>
  <header class="fixed top-0 left-0 right-0 bg-ink/95 backdrop-blur-sm border-b border-line z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo and museum name -->
        <div class="flex items-center">
          <router-link to="/" class="flex items-center space-x-3">
            <h1 class="font-serif text-xl sm:text-2xl text-textMain">济南市博物馆</h1>
          </router-link>
        </div>

        <!-- Desktop navigation -->
        <nav class="hidden md:flex items-center space-x-8">
          <router-link to="/exhibitions" class="text-textSub hover:text-textMain transition-colors">
            展览
          </router-link>
          <router-link to="/events" class="text-textSub hover:text-textMain transition-colors">
            活动
          </router-link>
          <router-link to="/collections" class="text-textSub hover:text-textMain transition-colors">
            藏品
          </router-link>
          <router-link to="/visit" class="text-textSub hover:text-textMain transition-colors">
            参观指南
          </router-link>
          <router-link to="/about" class="text-textSub hover:text-textMain transition-colors">
            关于我们
          </router-link>
        </nav>

        <!-- Reserve button and mobile menu -->
        <div class="flex items-center space-x-4">
          <router-link
            to="/reserve"
            class="hidden md:inline-flex items-center px-4 py-2 bg-spring text-ink rounded-lg font-medium hover:bg-springHover transition-colors"
          >
            门票预约
          </router-link>

          <!-- Mobile menu button -->
          <button
            @click="mobileMenuOpen = !mobileMenuOpen"
            class="md:hidden p-2 rounded-lg text-textSub hover:text-textMain hover:bg-panel"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path v-if="!mobileMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
              <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile menu -->
    <div v-if="mobileMenuOpen" class="md:hidden bg-panel border-t border-line">
      <div class="px-4 py-4 space-y-3">
        <router-link
          to="/exhibitions"
          class="block px-4 py-2 text-textSub hover:text-textMain hover:bg-panel2 rounded-lg transition-colors"
          @click="mobileMenuOpen = false"
        >
          展览
        </router-link>
        <router-link
          to="/events"
          class="block px-4 py-2 text-textSub hover:text-textMain hover:bg-panel2 rounded-lg transition-colors"
          @click="mobileMenuOpen = false"
        >
          活动
        </router-link>
        <router-link
          to="/collections"
          class="block px-4 py-2 text-textSub hover:text-textMain hover:bg-panel2 rounded-lg transition-colors"
          @click="mobileMenuOpen = false"
        >
          藏品
        </router-link>
        <router-link
          to="/visit"
          class="block px-4 py-2 text-textSub hover:text-textMain hover:bg-panel2 rounded-lg transition-colors"
          @click="mobileMenuOpen = false"
        >
          参观指南
        </router-link>
        <router-link
          to="/about"
          class="block px-4 py-2 text-textSub hover:text-textMain hover:bg-panel2 rounded-lg transition-colors"
          @click="mobileMenuOpen = false"
        >
          关于我们
        </router-link>
        <router-link
          to="/reserve"
          class="block w-full px-4 py-3 bg-spring text-ink rounded-lg font-medium text-center hover:bg-springHover transition-colors"
          @click="mobileMenuOpen = false"
        >
          门票预约
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const mobileMenuOpen = ref(false)
</script>