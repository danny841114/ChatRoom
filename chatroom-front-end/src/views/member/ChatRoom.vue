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
        <button class="btn btn-primary" @click="sendMessage">送出</button>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from "vue";
import { Client } from "@stomp/stompjs";
import { useAuthStore } from "@/stores/auth";
import SockJS from "sockjs-client/dist/sockjs";
import axios from "axios";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const authStore = useAuthStore();
const chatRooms = ref([]);
const chatRoomName = ref("");
const chatRoomId = ref("");
const message = ref("");
const messages = ref([]);

const loadChatRooms = async () => {
  try {
    const response = await axios.get(`${apiBase}/api/chat/rooms`, {
      params: { userId: authStore.userId },
      headers: {
        Authorization: `Bearer ${authStore.token}`,
      },
    });

    chatRooms.value = response.data;
  } catch (e) {
    console.error("Error loading chat rooms", error);
  }
};

loadChatRooms();

const loadMessages = async (roomId) => {
  try {
    const response = await axios.get(`${apiBase}/api/chat/rooms/${roomId}`, {
      params: { userId: authStore.userId },
      headers: {
        Authorization: `Bearer ${authStore.token}`,
      },
    });

    chatRoomId.value = response.data.roomId;
    chatRoomName.value = response.data.name;
  } catch (e) {
    console.error("Error loading chat room description", error);
  }

  try {
    const response2 = await axios.get(
      `${apiBase}/api/chat/rooms/${roomId}/messages`,
      {
        params: { userId: authStore.userId },
        headers: {
          Authorization: `Bearer ${authStore.token}`,
        },
      }
    );

    messages.value = response2.data;
  } catch (e) {
    console.error("Error loading chat room messages", error);
  }
};

let stompClient = null;
let subscription = null;

const connectWebSocket = () => {
  stompClient = new Client({
    webSocketFactory: () => new SockJS(`${apiBase}/ws`),
    connectHeaders: {
      Authorization: `Bearer ${authStore.token}`,
    },
    debug: (str) => {
      console.log(str);
    },
    reconnectDelay: 5000,

    onConnect: () => {
      console.log("WebSocket connected");
    },

    onStompError: (frame) => {
      console.error("STOMP error", frame);
    },
  });

  stompClient.activate();
};

const subscribeRoom = (roomId) => {
  if (subscription) {
    subscription.unsubscribe();
  }

  subscription = stompClient.subscribe(
    `/topic/rooms/${roomId}`,
    (messageBody) => {
      const newMessage = JSON.parse(messageBody.body);
      messages.value.push(newMessage);
    }
  );
};

const sendMessage = () => {
  if (!message.value.trim()) return;

  stompClient.publish({
    destination: `/app/chat.sendMessage/${chatRoomId.value}`,
    body: JSON.stringify({
      senderId: authStore.userId,
      content: message.value,
    }),
  });

  message.value = "";
};

onMounted(() => {
  connectWebSocket();
});

onBeforeUnmount(() => {
  if (subscription) {
    subscription.unsubscribe();
  }

  if (stompClient) {
    stompClient.deactivate();
  }
});

watch(chatRoomId, (newRoomId) => {
  if (newRoomId === undefined || newRoomId === null || newRoomId === "") {
    return;
  }

  console.log("newRoomId", newRoomId);

  subscribeRoom(newRoomId);
});
</script>

<style scoped>
.chat-layout {
  display: flex;
  height: 85vh;
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
