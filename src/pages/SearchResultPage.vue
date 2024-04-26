<template>
    <user-card-list  :user-list="userList"/>
    <van-empty image="search" description="未找到这样的小伙伴" v-if="userList.length === 0" />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import {useRouter, useRoute} from "vue-router";
import myAxios from "../plugins/myAxios.js";
import qs from "qs";
import UserCardList from "../components/UserCardList.vue";
import {getUserAccount} from "../config/globalParameter.js";
import {toastFail} from "../plugins/toastUtils.js";

const router = useRouter();
const route = useRoute();


const id = route.params.id;
const res = ref();
const userList = ref([]);
onMounted(async () => {
    if (id === '1') {
        const {tags} = route.query;
        res.value = await myAxios.get('/user/search/tags', {
            params: {
                tagNameList: tags,
                userAccount: getUserAccount(),
            },
            paramsSerializer: params => {
                return qs.stringify(params, {indices: false})
            }
        })
    } else if (id === '2') {
        const { searchText } = route.query;
        res.value = await myAxios.get('/user/search/info', {
            params: {
                userInfo: searchText,
                userAccount: getUserAccount(),
            },
            paramsSerializer: params => {
                return qs.stringify(params, {indices: false})
            }
        })
    } else {
        toastFail('出错了!!!');
        return;
    }

    if (res.value.code === 10000) {
        const userListData = res.value.data;
        if (userListData) {
            userListData.forEach(user => {
                if (user.tags) {
                    // @ts-ignore
                    user.tags = JSON.parse(user.tags);
                }
            })
            // @ts-ignore
            userList.value = userListData;
        }
    } else {
        toastFail('数据查询失败: ' + res.value.description);
    }
})

</script>

<style scoped>

</style>