<template>
  <div class="container mt-5">
    <h3>查詢喜愛清單</h3>
    <table class="table table-hover">
      <thead>
        <tr>
          <th scope="col" class="text-center">#</th>
          <th scope="col" class="text-center">名稱</th>
          <th scope="col" class="text-center">價格</th>
          <th scope="col" class="text-center">數量</th>
          <th scope="col" class="text-center">總費率</th>
          <th scope="col" class="text-center">總金額</th>
          <th scope="col" class="text-center">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr
          style="height: 100px"
          v-for="(item, index) in likeListData"
          :key="index"
        >
          <th class="text-center">{{ index + 1 }}</th>

          <td class="text-center">{{ item.product.name }}</td>
          <td class="text-center">{{ item.product.price }}</td>
          <td class="text-center">{{ item.purchaseQuantity }}</td>
          <td class="text-center">{{ item.totalFee }}</td>
          <td class="text-center">{{ item.totalAmount }}</td>
          <td class="align-middle text-center">
            <a class="btn btn-success" @click="modifyProduct(item.sn)">修改</a>
            <a
              type="button"
              class="btn btn-danger"
              @click="deleteLikeList(item.sn)"
              style="margin-left: 10px"
              >刪除</a
            >
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import axios from "axios";
import Swal from "sweetalert2";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const authStore = useAuthStore();
const router = useRouter();
const likeListData = ref([]);

/* 獲取喜愛清單列表 */
const getLikeLists = async () => {
  const response = await axios.get(`${apiBase}/likeList`, {
    params: {
      userId: authStore.userId,
    },
  });

  likeListData.value = response.data.likeLists;
};
onMounted(getLikeLists);

/* 修改清單 */
const modifyProduct = async (sn) => {
  router.push({ path: "/product/modify", query: { sn } });
};

/* 刪除清單 */
const deleteLikeList = async (sn) => {
  const ask = await Swal.fire({
    title: "確定刪除？",
    icon: "warning",
    allowOutsideClick: false,
    showCancelButton: true,
    confirmButtonText: "確認",
    cancelButtonText: "返回",
  });

  if (!ask.isConfirmed) return;

  try {
    await axios.delete(`${apiBase}/likeList`, {
      params: {
        sn: sn,
      },
    });

    // 從畫面移除
    likeListData.value = likeListData.value.filter((item) => item.sn !== sn);
  } catch (error) {
    console.error("刪除 likeList 失敗:", error);

    await Swal.fire({
      title: "刪除商品失敗",
      icon: "error",
      allowOutsideClick: false,
      showCancelButton: false,
      confirmButtonText: "確認",
    });
  }
};
</script>

<style></style>
