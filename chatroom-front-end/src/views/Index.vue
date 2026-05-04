<template>
  <div class="container mt-5">
    <h3>首頁</h3>
    <div>&nbsp;</div>
    <div v-if="authStore.userId">
      <h5>歡迎 {{ authStore.username }}</h5>
    </div>
    <div v-else>
      <h5>請先登入</h5>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
</script>

<style></style>
