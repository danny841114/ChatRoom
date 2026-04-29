import { createRouter, createWebHistory } from "vue-router";
import Index from "@/views/Index.vue";
import AddProduct from "@/views/product/AddProduct.vue";
import ManageProduct from "@/views/product/ManageProduct.vue";
import ModifyProduct from "@/views/product/ModifyProduct.vue";
import Login from "@/views/member/Login.vue";
import ChatRoom from "@/views/member/ChatRoom.vue";

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
      path: "/product/add",
      name: "AddProduct",
      component: AddProduct,
    },
    {
      path: "/product/manage",
      name: "ManageProduct",
      component: ManageProduct,
    },
    {
      path: "/product/modify",
      name: "ModifyProduct",
      component: ModifyProduct,
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
