<template>
  <div class="chat-layout">
    <!-- 左側聊天室列表 -->
    <aside class="chat-sidebar">
      <h5>聊天室</h5>

      <div
        v-for="room in chatRooms"
        :key="room.roomId"
        class="room-item"
        @click="loadMessages(room.roomId)"
      >
        <div class="room-name">{{ room.name }}</div>
        <!-- <div class="room-type">
          {{ room.type === "PRIVATE" ? "私人聊天室" : "群組聊天室" }}
        </div> -->
      </div>
    </aside>

    <!-- 右側聊天室主體 -->
    <main class="chat-main">
      <div class="chat-header">
        {{ chatRoomName }}
      </div>

      <div class="messages">
        <div v-for="msg in messages" :key="msg.id" class="message">
          <strong>{{ msg.senderName }} ({{ msg.senderAccount }}) :</strong>
          {{ msg.content }}
        </div>
      </div>

      <div class="input-area">
        <input
          v-model="message"
          class="form-control"
          placeholder="輸入訊息..."
          @keyup.enter="sendMessage"
        />
        <button class="btn btn-primary" @click="sendMessage(1)">送出</button>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useAuthStore } from "@/stores/auth";
import axios from "axios";

const authStore = useAuthStore();
const chatRooms = ref([]);
const chatRoomName = ref("");
const message = ref("");
const messages = ref([]);

const getJwtToken = () => {
  return authStore.token;
};

const loadChatRooms = async () => {
  try {
    const response = await axios.get(
      "http://localhost:8080/api/chat/rooms",
      { params: { userId: authStore.userId } },
      {
        headers: {
          Authorization: `Bearer ${getJwtToken()}`,
        },
      },
    );

    chatRooms.value = response.data;
  } catch (e) {
    console.error("Error loading chat rooms", error);
  }
};

loadChatRooms();

// 發送訊息到後端
const sendMessage = (roomId) => {
  if (!message.value.trim()) return;

  // 構建訊息對象
  const msg = {
    content: message.value,
    senderId: authStore.userId, 
  };

  // 清空輸入框

  // 透過 Axios 發送訊息
  axios
    .post(
      `http://localhost:8080/api/chat/rooms/${roomId}/messages`,
      msg,
      {
        headers: {
          Authorization: "Bearer " + authStore.token, // 好像沒帶入
        },
      },
    )
    .then(() => {
      console.log("Message sent successfully");
      messages.value.push(msg); // 將訊息推送到畫面上
    })
    .catch((error) => {
      console.error("Error sending message", error);
    });
};

// 載入歷史訊息
const loadMessages = (chatRoomId) => {
  axios
    .get(
      `http://localhost:8080/api/chat/rooms/${chatRoomId}/messages`,
      { params: { userId: authStore.userId } },
      {
        headers: {
          Authorization: `Bearer ${getJwtToken()}`,
        },
      },
    )
    .then((response) => {
      messages.value = response.data;
      chatRoomName.value = response.data.roomName;
    })
    .catch((error) => {
      console.error("Error loading messages", error);
    });
};



</script>

<style scoped>
.chat-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.chat-sidebar {
  width: 260px;
  flex-shrink: 0;
  background-color: #f8f9fa;
  border-right: 1px solid #ddd;
  padding: 16px;
  overflow-y: auto;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.chat-header {
  height: 56px;
  padding: 16px;
  border-bottom: 1px solid #ddd;
  font-weight: bold;
}

.messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background-color: #fff;
}

.message {
  margin-bottom: 12px;
}

.input-area {
  display: flex;
  gap: 8px;
  padding: 12px;
  border-top: 1px solid #ddd;
}
</style>
