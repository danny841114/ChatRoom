<template>
  <nav
    class="navbar navbar-expand-lg navbar-dark bg-dark"
    style="
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      background-color: white;
      z-index: 1000;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    "
  >
    <div class="container-fluid">
      <a class="navbar-brand">ChatRoom</a>

      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarSupportedContent"
        aria-controls="navbarSupportedContent"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link class="nav-link" to="/">首頁</router-link>
          </li>
          <li class="nav-item" v-if="!authStore.account">
            <router-link class="nav-link" to="/login">登入</router-link>
          </li>
          <li class="nav-item" v-if="authStore.account">
            <span class="nav-link disabled">{{ authStore.username }}</span>
          </li>
          <li class="nav-item" v-if="authStore.account">
            <router-link class="nav-link" to="/chat">聊天</router-link>
          </li>
          <li class="nav-item" v-if="authStore.account">
            <a class="nav-link" @click="logout" style="cursor: pointer">登出</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>

  <!-- 這個空 div 用來補足 navbar 高度，避免下方內容被 navbar 擋住 -->
  <div style="height: 70px"></div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import Swal from "sweetalert2";

const router = useRouter();
const authStore = useAuthStore();

/* 登出 */
const logout = async () => {
  authStore.logout();

  await Swal.fire({
    title: "成功登出",
    icon: "success",
    allowOutsideClick: false,
    showCancelButton: false,
    confirmButtonText: "確認",
  });

  router.push({ path: "/" });
};
</script>

<style></style>
