<template>
    <van-nav-bar
            :title="mainPageName"
            left-arrow
            @click-left="onClickLeft"
            v-if="mountFlag"
    >
    <template #right>
        <van-popover v-model:show="showPopover" :actions="actions" @select="onSelect" placement="bottom-end">
            <template #reference>
                <van-button color="#FFFFFF" icon="bars" style="padding: 0"></van-button>
            </template>
        </van-popover>
    </template>
    </van-nav-bar>

    <div id="content">
        <router-view  />
    </div>

    <van-tabbar v-model="active" @change="onChange">
        <van-tabbar-item icon="home-o" name="云交友" to="/" >主页</van-tabbar-item>
        <van-tabbar-item icon="friends-o" name="队伍" to="/team">队伍</van-tabbar-item>
        <van-tabbar-item icon="search" name="搜索" to="/search">搜索</van-tabbar-item>
        <van-tabbar-item icon="contact-o" name="个人" to="/user">个人</van-tabbar-item>
    </van-tabbar>
</template>

<script setup>
import {showToast} from "vant";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.js";
import {
    getCurrentMainPageName,
    setCurrentMainPageName, setEditPageName,
    setLoginFlag,
    setUserAccount,
    setUserId
} from "../config/globalParameter.js"
import {toastAccept, toastError} from "../plugins/toastUtils.js";

const router = useRouter();

const active = ref('index');

const onClickLeft = () => {
    router.back();
};

const mountFlag = ref(false);
const mainPageName = ref('云交友');

const onChange = (index) => {
    mountFlag.value = false;
    // showToast(`标签 ${index}`);
    setCurrentMainPageName(index);
    mainPageName.value = getCurrentMainPageName();
    mountFlag.value = true;
};

onMounted(async () => {
    mainPageName.value = getCurrentMainPageName();
    mountFlag.value = true;
})

const showPopover = ref(false);

// 通过 actions 属性来定义菜单选项
const actions = [
    {
        id: 2,
        text: '添加标签'
    },
    {
        id: 1,
        text: '退出登录'
    },

];
const onSelect = async (action) => {
    if (action.id === 1) {
        const res = await myAxios.post('/user/logout');
        if (res.code === 10000) {
            toastAccept("退出登录成功");
        }
        else {
            toastError(res.description);
        }
        setLoginFlag(false);
        setUserAccount('');
        setUserId(0);
        await router.replace('/login');
    } else if (action.id === 2) {
        setEditPageName('添加标签');
        await router.push('/createTag');
        toastAccept('添加标签');
    }
};
</script>

<style scoped>
#content {
    padding-bottom: 60px;
}
</style>