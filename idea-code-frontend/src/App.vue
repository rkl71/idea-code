<!-- src/App.vue -->
<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { userLoginUserStore } from '@/stores/loginUser.ts'

const loginUserStore = userLoginUserStore();
loginUserStore.fetchLoginUser();

</script>

<template>
  <BasicLayout />
</template>

<style>

</style>
