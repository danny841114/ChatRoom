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
import { ref } from "vue";
import { useAuthStore } from "@/stores/auth";
import axios from "axios";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const emit = defineEmits(["close", "created"]);
const authStore = useAuthStore();
const props = defineProps({
  chatRoom: Object,
});

const deleteMember = (userId) => {};
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
