<template>
  <div class="container">
    <NavBar></NavBar>
    <div>
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import NavBar from "@/components/Navbar.vue";
</script>

<style scoped></style>
