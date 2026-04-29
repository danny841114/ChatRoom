import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token") || null,
    userId: localStorage.getItem("userId") || null,
    username: localStorage.getItem("username") || null,
  }),

  actions: {
    setLogin(data) {
      this.token = data.token;
      this.userId = data.userId;
      this.username = data.username;

      // 持久化（刷新不會掉）
      localStorage.setItem("token", data.token);
      localStorage.setItem("userId", data.userId);
      localStorage.setItem("username", data.username);
    },

    logout() {
      this.token = null;
      this.userId = null;
      this.username = null;

      localStorage.removeItem("token");
      localStorage.removeItem("userId");
      localStorage.removeItem("username");
    },
  },
});
