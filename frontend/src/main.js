// main.js 或 main.ts
import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css' // 確保引入樣式
import router from "@/router/index.js";
import axios from "axios";

axios.defaults.baseURL = 'http://localhost:8080'

const app = createApp(App)

app.use(ElementPlus) // 使用 Element Plus
app.use(router)

app.mount('#app')
