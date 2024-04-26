<template>
    <div v-if="isMount">
        <van-form>
            <van-cell-group inset style="margin: 10px 10px;">
                <van-field
                    label="队伍头像"
                    readonly
                >
                    <template #input>
                        <div style="text-align: right">
                            <van-image style="height: 50px;" :src='teamInfo.teamAvatar' @click="previewAvatar">
                                <template #loading>
                                    <van-loading type="spinner" size="10"/>
                                </template>
                            </van-image>
                        </div>
                    </template>
                </van-field>
                <van-field
                    v-model="teamInfo.teamName"
                    label="队伍名称"
                    readonly
                />
                <van-field
                    v-model="teamInfo.teamDescription"
                    label="队伍描述"
                    readonly
                />
                <van-field
                    v-model="teamUser"
                    label="队伍人数"
                    readonly
                />
                <van-field
                    v-model="teamState"
                    label="队伍状态"
                    readonly
                />
                <van-field
                    v-model="teamExpireTime"
                    label="队伍过期时间"
                    readonly
                />
                <van-field readonly label="队伍成员">
                    <template #input>
                        <van-image round height="50px" style="margin: 0 2px" v-for="user in teamInfo.userList" :src="user.avatarUrl" @click="clickAvatar(user.id)"/>
                    </template>
                    <template #right-icon>
                        <van-icon color="#1989FA" :name=arrowDir @click="changeArrowDir">展开</van-icon>
                    </template>
                </van-field>
                <div :hidden="hideDetail">
                    <user-card-list :user-list="userList" />
                </div>
            </van-cell-group>
            <van-button v-if="teamInfo.captainId == getUserId()" type="primary" round size="large" @click="toEditTeam(teamInfo.id)">编辑队伍信息</van-button>
            <van-button v-if="teamInfo.captainId == getUserId()" type="danger" round size="large" @click="deleteTeam(teamInfo.id)" style="margin: 10px 0">解散队伍</van-button>
            <van-button v-if="containTeam()" type="warning" round size="large" @click="quitTeam()">退出队伍</van-button>
        </van-form>
    </div>
</template>

<script setup>
import {useRoute, useRouter} from "vue-router";
import { ref, onMounted } from 'vue';
import {getUserId, setEditPageName} from "../config/globalParameter.js";
import MyAxios from "../plugins/myAxios.js";
import UserCardList from "../components/UserCardList.vue";
import {showConfirmDialog, showImagePreview, showNotify} from "vant";
import {toastError, toastFail} from "../plugins/toastUtils.js";

const route = useRoute();
const router = useRouter();
const isMount = ref(false)

const teamInfo = ref();
const teamState = ref('公共');
const teamUser = ref();
const teamExpireTime = ref();
const userList = ref([]);

onMounted(async () => {
    setEditPageName('队伍');
    const { teamId } = route.query;
    const res = await MyAxios.get('/team/get', { params: { teamId: teamId } });
    if (res.code === 10000) {
        teamInfo.value = res.data;
        const status = teamInfo.value.teamStatus;
        teamState.value = status === 0 ? '公共' : status === 1 ? '私有' : '加密';
        teamUser.value = teamInfo.value.currentNum + '/' + teamInfo.value.maxNum;
        teamExpireTime.value = teamInfo.value.expireTime.toString().substring(0, 10);
        userList.value = teamInfo.value.userList;
        userList.value.forEach(user => {
            if (user.tags) {
                // @ts-ignore
                user.tags = JSON.parse(user.tags);
            }
        })
        isMount.value = true;
    } else {
        toastError(res.description);
        await router.replace('/');
    }
})

const clickAvatar = (userId) => {
    setEditPageName('用户');
    router.push({
        path: '/userInfo',
        query: {
            userId: userId
        }
    });
}

const toEditTeam = (teamId) => {
    setEditPageName('队伍信息');
    router.push({
        path: '/team/edit',
        query: {
            teamId: teamId
        }
    })
}

const arrowDir = ref("arrow-up");
const hideDetail = ref(true);
const changeArrowDir = () => {
    hideDetail.value = !hideDetail.value;
    arrowDir.value = arrowDir.value === "arrow-up" ? "arrow-down" : "arrow-up";
}

const deleteTeam = async () => {
    showConfirmDialog({
        title: '解散队伍',
        message: '队伍将会永久消失, 确认解散这个队伍吗?'
    }).then( async () => {
        const res = await MyAxios.post('/team/delete', { id: teamInfo.value.id });
        if (res.code === 10000) {
            showNotify('解散队伍成功!');
        } else {
            showNotify({
                type: 'danger',
                message: res.description,
                duration: 1000
            });
        }
        console.log('解散队伍了');
        router.back();
    }).catch( () => {
        console.log('取消解散队伍');
    })
}

const quitTeam = async () => {
    showConfirmDialog({
        title: '退出队伍',
        message: '确认退出这个队伍吗?'
    }).then( async () => {
        const res = await MyAxios.post('/team/quit', { teamId: teamInfo.value.id });
        if (res.code === 10000) {
            showNotify('退出队伍成功!');
        } else {
            showNotify({
                type: 'danger',
                message: res.description,
                duration: 1000
            });
        }
        console.log('退出队伍了');
        router.back();
    }).catch( () => {
        console.log('取消退出队伍');
    })
}

const containTeam = () => {
    const userId = getUserId();
    userList.value.forEach(user => {
        if (user.id == userId) {
            return true;
        }
    })
    return false;
}

const previewAvatar = () => showImagePreview({
    images: [teamInfo.value.teamAvatar.toString()],
    closeable: true,
});

</script>

<style scoped>

</style>