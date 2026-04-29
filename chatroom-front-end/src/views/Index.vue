<template>
  <div class="container mt-5">
    <h3>首頁</h3>
    <div>&nbsp;</div>
    <div v-if="authStore.userId">
      <h5>歡迎 {{ authStore.username }}</h5>
    </div>
    <div v-else>
      <h5>選擇登入身分</h5>
      <a class="btn btn-primary" @click="login('U001', 'aaa123')">U001</a>&nbsp;
      <a class="btn btn-primary" @click="login('U002', 'bbb456')">U002</a>&nbsp;
      <a class="btn btn-primary" @click="login('U003', 'ccc789')">U003</a>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "@/stores/auth";
import axios from "axios";
import Swal from "sweetalert2";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const authStore = useAuthStore();

const login = async (id, pwd) => {
  try {
    const response = await axios.post(`${apiBase}/login`, {
      userId: id,
      password: pwd,
    });

    authStore.setLogin(response.data);

    await Swal.fire({
      title: "成功登入",
      icon: "success",
      allowOutsideClick: false,
      showCancelButton: false,
      confirmButtonText: "確認",
    });
  } catch (error) {
    console.error("登入失敗:", error);

    await Swal.fire({
      title: "登入失敗",
      icon: "error",
      allowOutsideClick: false,
      showCancelButton: false,
      confirmButtonText: "確認",
    });
  }
};
</script>

<style></style>
