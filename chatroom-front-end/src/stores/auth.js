import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token") || null,
    account: localStorage.getItem("account") || null,
    username: localStorage.getItem("username") || null,
    userId: localStorage.getItem("userId") || null,
  }),

  actions: {
    setLogin(data) {
      this.token = data.token;
      this.account = data.account;
      this.username = data.username;
      this.userId = data.userId;

      // 持久化（刷新不會掉）
      localStorage.setItem("token", data.token);
      localStorage.setItem("account", data.account);
      localStorage.setItem("username", data.username);
      localStorage.setItem("userId", data.userId);
    },

    logout() {
      this.token = null;
      this.account = null;
      this.username = null;
      this.userId = null;

      localStorage.removeItem("token");
      localStorage.removeItem("account");
      localStorage.removeItem("username");
      localStorage.removeItem("userId");
    },
  },
});
