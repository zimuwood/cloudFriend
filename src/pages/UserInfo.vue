<template>
    <div v-if="isMount">
        <van-cell title="头像">
            <van-image height="60px" :src="user.avatarUrl" radius="10px">
                <template v-slot:loading>
                    <van-loading type="spinner" size="20"/>
                </template>
            </van-image>
        </van-cell>
        <van-cell title="昵称" :value="user.username"/>
        <van-cell title="账号" :value="user.userAccount"/>
        <van-cell title="性别">
            <template #value>
                <span v-show="user.gender === 0">男</span>
                <span v-show="user.gender === 1">女</span>
            </template>
        </van-cell>
        <van-cell title="标签">
            <template #value>
                <van-tag v-for="(tag,index) in user.tags" :show="true" size="small" type="primary">
                    {{ tag }}
                </van-tag>
            </template>
        </van-cell>
        <van-cell title="简介" :value="user.profile" />
        <van-cell title="电话" :value="user.phone" />
        <van-cell title="邮箱" :value="user.email" />
        <van-cell title="注册时间" :value="user.createTime.toString().substring(0, 10)"/>
    </div>
</template>

<script setup>
import {useRoute, useRouter} from "vue-router";
import { ref, onMounted } from 'vue';
import MyAxios from "../plugins/myAxios.js";
import {setEditPageName} from "../config/globalParameter.js";
import {toastError} from "../plugins/toastUtils.js";

const route = useRoute();
const router = useRouter();
const isMount = ref(false)

const user = ref();

onMounted(async () => {
    setEditPageName('用户');
    const { userId } = route.query;
    const res = await MyAxios.get('/user/get', { params: { userId: userId } });
    if (res.code === 10000) {
        user.value = res.data;
        user.value.tags = JSON.parse(user.value.tags);
        isMount.value = true;
    } else {
        toastError(res.description);
        await router.replace('/');
    }
    isMount.value = true;
})

</script>

<style scoped>

</style>