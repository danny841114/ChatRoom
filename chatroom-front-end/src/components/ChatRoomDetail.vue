<template>
  <div class="modal-backdrop-custom">
    <div class="modal-content-custom">
      <h5 class="text-center">聊天室成員</h5>

      <ul class="list-group">
        <li
          class="list-group-item d-flex justify-content-between align-items-center"
          v-for="u in props.chatRoom.users"
          :key="u.id"
        >
          <span>{{ u.account }} ({{ u.username }})</span>
          <button
            class="btn btn-danger"
            v-if="chatRoom.type === 'GROUP'"
            @click="deleteMember(u.id)"
          >
            移除
          </button>
        </li>
      </ul>

      <div class="mt-3 text-center">
        <button class="btn btn-primary" v-if="chatRoom.type === 'GROUP'">
          新增
        </button>
        &nbsp;
        <button class="btn btn-primary" @click="$emit('close')">關閉</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "@/stores/auth";
import axios from "axios";
import Swal from "sweetalert2";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const emit = defineEmits(["close", "created"]);
const authStore = useAuthStore();
const props = defineProps({
  chatRoom: Object,
});

const deleteMember = async (userId) => {
  const ask = await Swal.fire({
    title: "移除聊天室成員",
    icon: "warning",
    text: `確定移除${userId}?`,
    showCancelButton: true,
    confirmButtonText: "確定",
    cancelButtonText: "返回",
  });

  if (!ask.isConfirmed) return;

  try {
    const response = await axios.delete(
      `${apiBase}/api/chat/rooms/${props.chatRoom.roomId}/user/${userId}`,
      {
        params: { userId: authStore.userId },
      },
    );

    if (response.status === 204) {
      const index = props.chatRoom.users.findIndex((u) => u.id === userId);

      if (index !== -1) props.chatRoom.users.splice(index, 1);
    }
  } catch (e) {
    console.error("Error delete member from chat room", e);
  }
};
</script>

<style scoped>
.modal-backdrop-custom {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);

  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content-custom {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 40%;
  height: auto;
}
</style>
