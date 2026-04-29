<template>
  <div class="container mt-5">
    <h3>修改喜愛清單</h3>
    <form @submit.prevent="modifyProduct">
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

      <!-- 商品名稱 -->
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

      <!-- 商品價格 -->
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
      <a
        class="btn btn-secondary"
        @click="getProduct(sn)"
        style="margin-left: 10px"
        >重設</a
      >
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
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import axios from "axios";
import Swal from "sweetalert2";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();
const account = ref();
const productName = ref();
const productPrice = ref();
const productQuantity = ref();
const feeRate = ref();
const sn = route.query?.sn;

/* 獲取商品資訊 */
const getProduct = async (id) => {
  const response = await axios.get(`${apiBase}/likeList/detail`, {
    params: {
      sn: id,
    },
  });

  const data = response.data;
  account.value = data.account;
  productName.value = data.product?.name;
  productPrice.value = data.product?.price;
  productQuantity.value = data.purchaseQuantity;
  feeRate.value = data.product?.feeRate;
};
onMounted(() => getProduct(sn)); // 傳入一個「函式」，不是執行一個函式的結果

/* 修改商品 */
const modifyProduct = async () => {
  const ask = await Swal.fire({
    title: "確定修改？",
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
    const response = await axios.put(`${apiBase}/likeList/modify`, {
      sn: sn,
      userId: authStore.userId,
      account: account.value,
      productName: productName.value,
      price: price.value,
      quantity: productQuantity.value,
      feeRate: feeRate.value,
    });

    router.push({ path: "/product/manage" });
  } catch (error) {
    console.error("修改商品失敗:", error);

    await Swal.fire({
      title: "修改商品失敗",
      icon: "error",
      allowOutsideClick: false,
      showCancelButton: false,
      confirmButtonText: "確認",
    });
  }
};
</script>

<style></style>
