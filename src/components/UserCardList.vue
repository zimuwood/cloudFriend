<template>
    <van-card
            v-for="user in userList"
            :title="`${user.username}（${user.userAccount}）`"
            @click-thumb="checkUser(user.id)"
    >
        <template #tag>
            <van-tag v-if="user.gender === 0" type="primary" mark>男</van-tag>
            <van-tag v-if="user.gender === 1" color="#f62a4d" mark>女</van-tag>
        </template>
        <template #thumb >
            <van-image :src="user.avatarUrl" height="100%" width="100%" show-error/>
        </template>
        <template #desc>
            <van-text-ellipsis :content="user.profile?.toString()" expand-text="展开" collapse-text="收起" style="color: #888888"/>
        </template>
        <template #tags>
            <van-tag plain type="primary" v-for="tag in user.tags?.slice(0,5)" style="margin-right: 2px; margin-top: 2px">
                {{ tag }}
            </van-tag>
            <span v-if="user.tags?.length > 5">...</span>
        </template>
        <template #price>
            <span v-text="user.email" />
        </template>
        <template #num>
            <van-button color="#F7F8FA" size="mini" style="color:#1989FA; font-size: 14px" @click="checkUser(user.id)" >详细信息</van-button>
        </template>
    </van-card>
</template>

<script setup lang="ts">
import {setEditPageName} from "../config/globalParameter.js";
import {useRouter} from "vue-router";

interface UserCardListProps {
    userList: API.UserType[];
}

// @ts-ignore
const props = withDefaults(defineProps<UserCardListProps>(), {
    userList: [] as API.UserType[],
});

const router = useRouter();

const checkUser = (userId) => {
    setEditPageName('用户');
    router.push({
        path: '/userInfo',
        query: {
            userId: userId
        }
    });
}

</script>

<style scoped>

</style>