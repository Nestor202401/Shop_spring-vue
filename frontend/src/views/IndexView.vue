<script setup>
import {logout} from "@/net";
import router from "@/router";

function userLogout(){
  logout( () => router.push('/'))
}
</script>

<template>
  <el-button @click="userLogout">退出登入</el-button>
</template>

<style scoped>

</style>