<template>
    <van-pull-refresh v-model="loading" @refresh="onRefresh" style="min-height: 80vh;">
        <template #loading>
            <p>正在为您寻找小伙伴中...</p>
        </template>
        <user-card-list  :user-list="userList"/>
        <van-empty description="数据为空" v-if="userList.length === 0" />
    </van-pull-refresh>
</template>

<script setup>
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue"
import myAxios from "../plugins/myAxios.js";
import UserCardList from "../components/UserCardList.vue";
import {toastAccept, toastFail} from "../plugins/toastUtils.js";

const router = useRouter();

const flashFlag = ref(0);

const userList = ref([]);
const refreshUserList = async () => {
    const res = flashFlag.value % 2 == 1 ? await myAxios.get('/user/match') : await myAxios.get('/user/recommend');

    if (res.code === 10000) {
        const userListData = res.data.records;
        if (userListData) {
            userListData.forEach(user => {
                if (user.tags) {
                    // @ts-ignore
                    user.tags = JSON.parse(user.tags);
                }
            });

            userList.value = userListData;
        }
        flashFlag.value % 2 == 1 ? toastAccept('匹配用户成功') : toastAccept('推荐用户成功');
        return true;
    } else {
        toastFail('数据查询失败: ' + res.description);
        return false;
    }
}
onMounted(async () => {
    await router.replace('/');
    await refreshUserList();
})

const loading = ref(false);
const onRefresh = () => {
    flashFlag.value += 1;
    setTimeout(() => {
        refreshUserList();
        loading.value = false;
    }, 1000);
};
</script>

<style scoped>

</style>