<template>
  <div class="chat-container">
    <div class="messages">
      <div v-for="(msg, index) in messages" :key="index" class="message">
        <strong>{{ msg.username }}:</strong> {{ msg.content }}
      </div>
    </div>

    <div class="input-area">
      <input
        v-model="message"
        type="text"
        placeholder="輸入訊息..."
        @keyup.enter="sendMessage"
      />
      <button @click="sendMessage">發送</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";

// 定義訊息列表與當前輸入的訊息
const message = ref("");
const messages = ref([]);

// 從 localStorage 獲取 JWT token
const getJwtToken = () => {
  return localStorage.getItem("jwt");
};

// 發送訊息到後端
const sendMessage = () => {
  if (!message.value.trim()) return;

  // 構建訊息對象
  const msg = {
    content: message.value,
    username: "User", // 這裡可以改為真實使用者名稱
  };

  // 清空輸入框
  message.value = "";

  // 透過 Axios 發送訊息
  axios
    .post(
      "http://localhost:8080/api/chat", // 後端聊天室訊息 API
      msg,
      {
        headers: {
          Authorization: `Bearer ${getJwtToken()}`,
        },
      }
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
const loadMessages = () => {
  axios
    .get("http://localhost:8080/api/chat", {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`,
      },
    })
    .then((response) => {
      messages.value = response.data.messages;
    })
    .catch((error) => {
      console.error("Error loading messages", error);
    });
};

// 在組件掛載後載入訊息
loadMessages();
</script>

<style scoped>
.chat-container {
  width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 10px;
  background-color: #f9f9f9;
}

.messages {
  height: 300px;
  overflow-y: scroll;
  margin-bottom: 20px;
}

.message {
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

.input-area {
  display: flex;
  justify-content: space-between;
}

.input-area input {
  width: 80%;
  padding: 10px;
}

.input-area button {
  padding: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
}

.input-area button:hover {
  background-color: #45a049;
}
</style>
