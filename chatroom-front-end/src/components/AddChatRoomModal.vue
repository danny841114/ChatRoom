<template>
  <div class="modal-backdrop-custom">
    <div class="modal-content-custom">
      <h5 class="center-flex">新增聊天室</h5>

      <div class="input-group mb-3">
        <span class="input-group-text">名稱</span>
        <input v-model="form.name" type="text" class="form-control" required />
      </div>

      <div class="input-group mb-3">
        <span class="input-group-text">型別</span>
        <select v-model="form.type" class="form-control">
          <option value="PRIVATE">PRIVATE</option>
          <option value="GROUP">GROUP</option>
        </select>
      </div>

      <div class="input-group mb-3">
        <span class="input-group-text">人員</span>
        <select v-model="form.memberIds" multiple class="form-control">
          <option v-for="u in users" :key="u.id" :value="u.id">
            {{ u.username }} ({{ u.account }})
          </option>
        </select>
      </div>

      <div class="mb-3 center-flex">
        <button class="btn btn-primary" @click="createRoom">建立</button>&nbsp;
        <button class="btn btn-primary" @click="$emit('close')">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useAuthStore } from "@/stores/auth";
import axios from "axios";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const emit = defineEmits(["close", "created"]);
const authStore = useAuthStore();
const users = ref([]);
const form = ref({
  name: "",
  type: "PRIVATE",
  memberIds: [],
});

const loadUsers = async () => {
  try {
    const response = await axios.get(`${apiBase}/api/chat/rooms/users`, {
      params: { userId: authStore.userId },
    });

    users.value = response.data;
  } catch (e) {
    console.error("Error loading users", error);
  }
};

const createRoom = async () => {
  try {
    await axios.post(
      `${apiBase}/api/chat/rooms`,

      form.value,
      {
        params: { userId: authStore.userId },
      }
    );

    emit("created");
  } catch (e) {
    console.error("Error creating room", error);
  }
};

onMounted(() => {
  loadUsers();
});
</script>

<style>
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
  width: 100vh;
  height: 70vh;
}

.center-flex {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
