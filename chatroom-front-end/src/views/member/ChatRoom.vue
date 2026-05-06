<template>
  <div class="chat-layout">
    <!-- 左側聊天室列表 -->
    <aside class="chat-sidebar">
      <h4>聊天室</h4>

      <button class="btn btn-primary mb-3" @click="openModal">新增聊天</button>

      <div
        v-for="room in chatRooms"
        :key="room.roomId"
        class="list-group"
        style="cursor: pointer"
        @click="loadMessages(room.roomId)"
      >
        <div
          class="list-group-item list-group-item-action room-name d-flex justify-content-between"
        >
          <span>{{ room.name }} </span>
          <span class="unread-badge" v-if="room.unreadMessagesCount > 0">{{
            room.unreadMessagesCount
          }}</span>
        </div>
        <!-- <div class="room-type">
          {{ room.type === "PRIVATE" ? "私人聊天室" : "群組聊天室" }}
        </div> -->
      </div>
    </aside>

    <!-- 右側聊天室主體 -->
    <main class="chat-main">
      <div
        class="chat-header"
        style="cursor: pointer"
        @click="openChatRoomDetailModal"
      >
        {{ currentChatRoom.name }}
      </div>

      <div class="messages">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message"
          :class="{
            'my-message': Number(msg.senderId) === Number(authStore.userId),
          }"
        >
          <strong>{{ msg.senderName }} ({{ msg.senderAccount }}) :</strong>
          {{ msg.content }}
          <div class="message-time">
            {{ formatTime(msg.createdAt) }}
          </div>
        </div>
      </div>

      <div class="input-area">
        <input
          v-model="message"
          class="form-control"
          placeholder="輸入訊息..."
          :disabled="isChatRoomIdEmpty"
          @keyup.enter="sendMessage"
        />
        <button
          class="btn btn-primary send-btn"
          :disabled="isChatRoomIdEmpty"
          @click="sendMessage"
        >
          送出
        </button>
      </div>
    </main>
  </div>

  <AddChatRoomModal
    v-show="showAddChatRoomModal"
    @close="showAddChatRoomModal = false"
    @created="handleCreated"
  ></AddChatRoomModal>

  <ChatRoomDetail
    v-show="showChatRoomDetailModal"
    :chat-room="currentChatRoom"
    @close="showChatRoomDetailModal = false"
  ></ChatRoomDetail>
</template>

<script setup>
import { ref, watch, computed, onMounted, onBeforeUnmount } from "vue";
import { Client } from "@stomp/stompjs";
import { useAuthStore } from "@/stores/auth";
import SockJS from "sockjs-client";
import axios from "axios";
import AddChatRoomModal from "@/components/AddChatRoomModal.vue";
import ChatRoomDetail from "@/components/ChatRoomDetail.vue";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const authStore = useAuthStore();
const chatRooms = ref([]);
const currentChatRoom = ref({
  roomId: "",
  name: "",
  users: [],
});
const message = ref("");
const messages = ref([]);
const showAddChatRoomModal = ref(false);
const showChatRoomDetailModal = ref(false);

const isChatRoomIdEmpty = computed(() => {
  return (
    typeof currentChatRoom.value.roomId === "string" &&
    currentChatRoom.value.roomId.trim() === ""
  );
});

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
    console.error("Error loading chat rooms", e);
  }
};

const loadMessages = async (roomId) => {
  try {
    const response = await axios.get(`${apiBase}/api/chat/rooms/${roomId}`, {
      params: { userId: authStore.userId },
      headers: {
        Authorization: `Bearer ${authStore.token}`,
      },
    });

    currentChatRoom.value = response.data;
  } catch (e) {
    console.error("Error loading chat room description", e);
  }

  try {
    const response2 = await axios.get(
      `${apiBase}/api/chat/rooms/${roomId}/messages`,
      {
        params: { userId: authStore.userId },
        headers: {
          Authorization: `Bearer ${authStore.token}`,
        },
      },
    );

    messages.value = response2.data;
  } catch (e) {
    console.error("Error loading chat room messages", e);
  }

  // update unread message count 0
  const room = chatRooms.value.find((r) => r.roomId === roomId);
  if (room) room.unreadMessagesCount = 0;
};

const formatTime = (timeStr) => {
  const date = new Date(timeStr);

  return date.toLocaleString("zh-TW", {
    timeZone: "Asia/Taipei",
    hour12: false,
  });
};

const openModal = () => {
  showAddChatRoomModal.value = true;
};

const handleCreated = () => {
  showAddChatRoomModal.value = false;
  loadChatRooms();
};

const openChatRoomDetailModal = () => {
  showChatRoomDetailModal.value = true;
};

let stompClient = null;
let subscription = null;
let roomListSubscription = null;

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
      subscribeRoomList(); // 先訂閱聊天室動態
    },

    onStompError: (frame) => {
      console.error("STOMP error", frame);
    },
  });

  stompClient.activate();
};

const subscribeRoom = (roomId) => {
  if (subscription) subscription.unsubscribe();

  subscription = stompClient.subscribe(
    `/topic/rooms/${roomId}`,
    (messageBody) => {
      const newMessage = JSON.parse(messageBody.body);
      messages.value.push(newMessage);
    },
  );
};

const subscribeRoomList = () => {
  if (roomListSubscription) roomListSubscription.unsubscribe();

  roomListSubscription = stompClient.subscribe(
    `/topic/users/${authStore.userId}/rooms`,
    (messageBody) => {
      const updatedRooms = JSON.parse(messageBody.body);
      chatRooms.value = updatedRooms;
    },
  );
};

const sendMessage = () => {
  if (!message.value.trim()) return;

  stompClient.publish({
    destination: `/app/chat.sendMessage/${currentChatRoom.value.roomId}`,
    body: JSON.stringify({
      senderId: authStore.userId,
      content: message.value,
    }),
  });

  message.value = "";
};

onMounted(() => {
  loadChatRooms();
  connectWebSocket();
});

onBeforeUnmount(() => {
  if (subscription) subscription.unsubscribe();
  if (roomListSubscription) roomListSubscription.unsubscribe();
  if (stompClient) stompClient.deactivate();
});

watch(currentChatRoom, (currentChatRoom) => {
  const newRoomId = currentChatRoom.roomId;
  if (newRoomId === undefined || newRoomId === null || newRoomId === "") return;

  subscribeRoom(newRoomId);
  if (!roomListSubscription) subscribeRoomList();
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

.my-message {
  margin-left: auto;
  text-align: right;
}

.message-time {
  font-size: 12px;
  color: gray;
  margin-top: 4px;
}

.input-area {
  display: flex;
  gap: 8px;
  padding: 12px;
  border-top: 1px solid #ddd;
}

.room-item {
  cursor: pointer;
  padding-top: 10px;
}

.room-name {
  white-space: nowrap; /* 不換行 */
  overflow: hidden; /* 超出隱藏 */
  text-overflow: ellipsis; /* 顯示 ... */
}

.send-btn {
  width: 70px;
}

.unread-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  min-width: 22px;
  height: 22px;
  padding: 0 6px;

  background-color: red;
  color: white;

  border-radius: 999px;
  font-size: 12px;
  font-weight: bold;
  line-height: 1;
}
</style>
