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

    logout() {
      this.account = null;
      this.username = null;
      this.userId = null;
    },

    // async fetchMe() {
    //   try {
    //     const response = await axios.get(`${apiBase}/api/me`, {
    //       withCredentials: true,
    //     });

    //     this.setLogin(response.data);
    //   } catch (e) {
    //     this.logout();
    //   }
    // },
  },
});
