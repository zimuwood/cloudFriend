<template>
    <router-view />
</template>

<script setup lang="ts">
import {getLoginFlag} from "./config/globalParameter.js";
import {toastFail} from "./plugins/toastUtils.js";
import {useRouter} from "vue-router";

const noAuthUrl = ['/login', '/register'];

const router = useRouter();

router.beforeEach(async (to, from, next) => {
    // 非权限页面直接放行
    if (noAuthUrl.includes(to.path)) {
        next();
    } else {
        // 检查用户是否已登录
        if (getLoginFlag().toString() === 'false') {
            // 将用户重定向到登录页面
            if (from.path != '/') {
                toastFail('请先登录!');
            }
            next('/login');
        } else {
            next();
        }
    }
})
</script>

<style scoped>

</style>
