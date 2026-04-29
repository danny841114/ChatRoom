<template>
    <div
      class="container mt-5 justify-content-center align-items-center"
      style="width: 300px"
    >
      <h3 class="text-center">登入</h3>
      <div>
        <form @submit.prevent="handleLogin">
          <div class="input-group mb-3">
            <span class="input-group-text">帳號</span>
            <input
              type="text"
              class="form-control"
              aria-label="account"
              v-model="account"
              required
            />
          </div>
  
          <div class="input-group mb-3">
            <span class="input-group-text">密碼</span>
            <input
              type="password"
              class="form-control"
              aria-label="account"
              v-model="password"
              required
            />
          </div>
  
          <div class="text-center">
            <button type="submit" class="btn btn-primary">登入</button>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from "vue";
  import { useRouter } from "vue-router";
  import { useAuthStore } from "@/stores/auth";
  import axios from "axios";
  import Swal from "sweetalert2";

  // const apiBase = import.meta.env.VITE_API_BASE_URL;
  const apiBase = "http://localhost:8080";
  const account = ref("");
  const password = ref("");
  const errorMsg = ref("");
  const router = useRouter();
  const authStore = useAuthStore();
  
  const handleLogin = async () => {
    try {
      const response = await axios.post(`${apiBase}/api/auth/login`, {
        account: account.value,
        password: password.value,
      });
      console.log("response.data",response.data)

      authStore.setLogin(response.data);
  
      await Swal.fire({
        title: "登入成功",
        icon: "success",
        text: "確定後跳轉首頁",
        confirmButtonText: "確定",
      });
  
      router.replace("/");
    } catch (error) {
      errorMsg.value = error.response.data.message;
      await Swal.fire({
        title: "登入失敗",
        icon: "error",
        text: errorMsg.value,
        confirmButtonText: "確定",
      });
    }
  };
  </script>
  
  <style></style>