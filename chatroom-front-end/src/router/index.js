import { createRouter, createWebHistory } from "vue-router";
import Index from "@/views/Index.vue";
import Login from "@/views/Login.vue";
import ChatRoom from "@/views/ChatRoom.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "Index",
      component: Index,
      alias: "/index",
    },
    {
      path: "/login",
      name: "Login",
      component: Login,
    },
    {
      path: "/chat",
      name: "ChatRoom",
      component: ChatRoom,
    },
  ],
});

export default router;
