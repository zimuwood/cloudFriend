<template>
    <div v-if="isMounted">
        <van-cell title="头像" is-link @click="toEditAvatar('avatarUrl', '头像', currentUser.avatarUrl)">
            <van-image height="60px" :src="currentUser.avatarUrl" radius="10px">
                <template v-slot:loading>
                    <van-loading type="spinner" size="20"/>
                </template>
            </van-image>
        </van-cell>
        <van-cell title="昵称" is-link :value="currentUser.username"
                  @click="toEdit('username', '昵称', currentUser.username)"/>
        <van-cell title="账号" :value="currentUser.userAccount"/>
        <van-cell title="性别" is-link @click="toEditGender('gender', '性别', currentUser.gender)">
            <template #value>
                <span v-show="currentUser.gender === 0">男</span>
                <span v-show="currentUser.gender === 1">女</span>
            </template>
        </van-cell>
        <van-cell title="标签" is-link @click="toEditTags('tags', '标签', currentUser.tags)">
            <template #value>
                <van-tag v-for="(tag,index) in currentUser.tags.slice(0,3)" :show="true" size="small" type="primary">
                    {{ tag }}
                </van-tag>
                <span v-show="currentUser.tags.length > 3">...</span>
            </template>
        </van-cell>
        <van-cell title="简介" is-link :value="currentUser.profile as string"
                  @click="toEdit('profile', '简介', currentUser.profile)"/>
        <van-cell title="电话" is-link :value="currentUser.phone as string"
                  @click="toEdit('phone', '手机号码', currentUser.phone)"/>
        <van-cell title="邮箱" is-link :value="currentUser.email as string"
                  @click="toEdit('email', '邮箱', currentUser.email)"/>
        <van-cell title="注册时间" :value="currentUser.createTime"/>
    </div>
</template>

<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.js";
import {setEditPageName} from "../config/globalParameter.js";
import {toastFail} from "../plugins/toastUtils.js";

const currentUser = ref();
const isMounted = ref(false);
onMounted(async () => {
    const res = await myAxios.get('/user/current');
    // @ts-ignore
    if (res.code === 10000) {
        currentUser.value = res.data;
        currentUser.value.tags = JSON.parse(currentUser.value.tags);
        currentUser.value.createTime = currentUser.value.createTime.toString().substring(0, 10);
    } else {
        toastFail('获取用户信息失败');
    }
    isMounted.value = true;
})

const router = useRouter();
const toEdit = (editKey: string, editName: string, currentValue: string) => {
    setPageName(editName);
    router.push({
        path: 'user/edit',
        query: {
            editKey,
            editName,
            currentValue,
        }
    })
}

const toEditAvatar = (editKey: string, editName: string, currentValue: string) => {
    setPageName(editName);
    router.push({
        path: 'user/edit/avatar',
        query: {
            editKey,
            editName,
            currentValue,
        }
    })
}

const toEditGender = (editKey: string, editName: string, currentValue: number) => {
    setPageName(editName);
    router.push({
        path: 'user/edit/gender',
        query: {
            editKey,
            editName,
            currentValue,
        }
    })
}

const toEditTags = (editKey: string, editName: string, currentValue: string[]) => {
    setPageName(editName);
    router.push({
        path: 'user/edit/tags',
        query: {
            editKey,
            editName,
            currentValue,
        }
    })
}

const setPageName = (pageName) => {
    setEditPageName(pageName);
}
</script>

<style scoped>

</style>