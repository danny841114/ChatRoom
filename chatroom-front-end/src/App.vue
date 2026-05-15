<template>
  <div class="container">
    <NavBar></NavBar>
    <div>
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useAuthStore } from "@/stores/auth";
import NavBar from "@/components/Navbar.vue";

const authStore = useAuthStore();

onMounted(() => {
  authStore.fetchMe();
});
</script>

<style scoped></style>
