<template>
  <div class="login-container">
    <div class="login-box">
      <h2>登入</h2>
      <el-form :model="form" ref="formRef" :rules="rules">
        <el-form-item class="input-group" prop="username">
          <el-input
              v-model="form.username"
              placeholder="帳號 / 信箱"
              clearable
              class="input"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item class="input-group" prop="password">
          <el-input
              v-model="form.password"
              type="password"
              placeholder="密碼"
              show-password
              clearable
              class="input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <div style="text-align: left;">
              <el-form-item>
                <el-checkbox v-model="form.remember">記住我</el-checkbox>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="12">
            <div style="text-align: right;">
              <span class="text-link" >忘記密碼 ?</span>
              <span class="text-link" >註冊</span>
            </div>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="userLogin" class="login-button">登入</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {User, Lock} from '@element-plus/icons-vue'
import router from "@/router";
import {reactive, ref} from "vue";
import {login} from '@/net'

const formRef = ref()
const form = reactive({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [
    { required: true, message: '请输入用户名' }
  ],
  password: [
    { required: true, message: '请输入密码'}
  ]
}

function userLogin() {
  formRef.value.validate((isValid) => {
    if(isValid) {
      login(form.username, form.password, form.remember, () => router.push("/index"))
    }
  });
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-box {
  background-color: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 300px;
  text-align: center;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.input-group {
  margin-bottom: 15px;
  text-align: left;
}

.input {
  width: 100%;
  box-sizing: border-box;
}

.login-button {
  width: 100%;
  padding: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.login-button:hover {
  background-color: #45a049;
}

.text-link {
  color: #007bff;
  cursor: pointer;
  padding: 0 10px;
  font-size: 14px;
  text-decoration: none;
}

.text-link:hover {
  color: #0056b3;
  text-decoration: none;
}
</style>
