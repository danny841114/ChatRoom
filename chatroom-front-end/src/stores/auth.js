import { defineStore } from "pinia";
import axios from "axios";

const apiBase = import.meta.env.VITE_API_BASE_URL;

export const useAuthStore = defineStore("auth", {
  state: () => ({
    account: localStorage.getItem("account") || null,
    username: localStorage.getItem("username") || null,
    userId: localStorage.getItem("userId") || null,
  }),

  actions: {
    setLogin(data) {
      this.account = data.account;
      this.username = data.username;
      this.userId = data.userId;
    },

    async logout() {
      this.account = null;
      this.username = null;
      this.userId = null;

      try {
        const response = await axios.post(`${apiBase}/api/auth/logout`, null, {
          withCredentials: true,
        });
      } catch (e) {
        console.error("Error log out", e);
      }
    },

    async fetchMe() {
      try {
        const response = await axios.get(`${apiBase}/api/auth/fetchMe`, {
          withCredentials: true,
        });

        this.setLogin(response.data);
      } catch (e) {
        console.error("Error fetch me", e);
      }
    },
  },
});
