import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token") || null,
    account: localStorage.getItem("account") || null,
    username: localStorage.getItem("username") || null,
  }),

  actions: {
    setLogin(data) {
      this.token = data.token;
      this.account = data.account;
      this.username = data.username;

      // 持久化（刷新不會掉）
      localStorage.setItem("token", data.token);
      localStorage.setItem("account", data.account);
      localStorage.setItem("username", data.username);

      // 立即通知組件更新 Pinia 狀態
      // this.$state.token = data.token;
      // this.$state.account = data.account;
      // this.$state.username = data.username;
    },

    logout() {
      this.token = null;
      this.account = null;
      this.username = null;

      localStorage.removeItem("token");
      localStorage.removeItem("account");
      localStorage.removeItem("username");
    },
  },
});
