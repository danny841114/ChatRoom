<template>
  <div class="container mt-5">
    <h3>新增喜愛清單</h3>
    <form @submit.prevent="addProduct">
      <!-- 帳戶 -->
      <div class="input-group mb-3">
        <label class="input-group-text" for="account">帳戶</label>
        <input
          type="text"
          class="form-control"
          id="account"
          v-model="account"
          required
        />
      </div>

      <!-- 商品 -->
      <div class="input-group mb-3">
        <label class="input-group-text" for="productName">商品</label>
        <input
          type="text"
          class="form-control"
          id="productName"
          v-model="productName"
          required
        />
      </div>

      <!-- 價格 -->
      <div class="input-group mb-3">
        <label class="input-group-text" for="price">價格</label>
        <input
          type="number"
          class="form-control"
          id="price"
          min="1"
          v-model="productPrice"
          required
        />
      </div>

      <!-- 商品數量 -->
      <div class="input-group mb-3">
        <label class="input-group-text" for="quantity">數量</label>
        <input
          type="number"
          class="form-control"
          id="quantity"
          min="1"
          v-model="productQuantity"
          required
        />
      </div>

      <!-- 費率 -->
      <div class="input-group mb-3">
        <label class="input-group-text" for="feeRate">費率</label>
        <input
          type="number"
          class="form-control"
          id="feeRate"
          max="1"
          min="0"
          step="any"
          v-model="feeRate"
          required
        />
      </div>

      <button type="submit" class="btn btn-primary">送出</button>
      <router-link
        class="btn btn-secondary"
        to="/product/manage"
        style="margin-left: 10px"
        >返回</router-link
      >
    </form>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import axios from "axios";
import Swal from "sweetalert2";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const authStore = useAuthStore();
const router = useRouter();
const account = ref();
const productName = ref();
const productPrice = ref();
const productQuantity = ref();
const feeRate = ref();

/* 新增商品 */
const addProduct = async () => {
  const ask = await Swal.fire({
    title: "確定新增？",
    icon: "warning",
    allowOutsideClick: false,
    showCancelButton: true,
    confirmButtonText: "確認",
    cancelButtonText: "返回",
  });

  if (!ask.isConfirmed) {
    return;
  }

  try {
    const response = await axios.post(`${apiBase}/likeList/add`, {
      userId: authStore.userId,
      account: account.value,
      productName: productName.value,
      price: price.value,
      quantity: productQuantity.value,
      feeRate: feeRate.value,
    });

    router.push({ path: "/product/manage" });
  } catch (error) {
    console.error("新增喜愛清單失敗:", error);

    await Swal.fire({
      title: "新增商品失敗",
      icon: "error",
      allowOutsideClick: false,
      showCancelButton: false,
      confirmButtonText: "確認",
    });
  }
};
</script>

<style></style>
