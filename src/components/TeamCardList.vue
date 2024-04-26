<template>
    <van-card
            v-for="team in teamList"
            :title="team.teamName"
    >
        <template #title>
            <span style="font-size: 20px; font-weight: bold;">{{ team.teamName }}</span>
        </template>
        <template #tag>
            <van-tag v-if="team.captainId == getUserId()" type="primary" >队长</van-tag>
        </template>
        <template #thumb >
            <van-image :src="team.teamAvatar" style="margin-top: 10px" height="110%" width="105%" show-error/>
        </template>
        <template #tags>
            <div style="margin-bottom: 2px">
                <van-tag plain type="primary">{{ columns[team.teamStatus].name }}</van-tag>
                <div>队伍人数: {{ team.currentNum }}/{{ team.maxNum }}</div>
                <div>到期时间: {{ team.expireTime.toString().substring(0, 10) }}</div>
            </div>
        </template>
        <template #desc>
            <van-text-ellipsis :content="team.teamDescription" expand-text="展开" collapse-text="收起" style="color: #888888"/>
        </template>
        <template #price>
            <van-image round height="30px" v-for="user in team.userList.slice(0, 3)" :src="user.avatarUrl" />
            <van-icon name="ellipsis" size="large" v-if="team.userList.length > 3"/>
        </template>
        <template #num>
            <van-button color="#F7F8FA" size="mini" style="color:#1989FA; font-size: 14px" @click="checkTeam(team.id)">队伍详情</van-button>
            <van-button v-if="showJoinTeamButton(team.captainId, team.userList)" color="#F7F8FA" size="mini" style="color:#1989FA; font-size: 14px" @click="joinTeam(team.id, team.teamStatus)">加入队伍</van-button>
        </template>
    </van-card>
    <van-popup
        v-model:show="show"
        closeable
        close-icon="close"
        :style="{ height: '30%', width: '50%', padding: '40px'}"
    >
        <template #default>
            <van-form @submit="joinEncryptedTeam">
                <van-field
                    v-model="teamPassword"
                    label="入队密码"
                    type="password"
                    style="background-color: aliceblue"
                    :rules="[
                                {
                                    pattern: new RegExp('^\\w{6,12}$'),
                                    message: '密码是6-12位'
                                },
                                {
                                    required: true,
                                    message: '请输入密码'
                                }
                            ]"
                >
                </van-field>
                <van-button type="primary" size="large" native-type="submit">
                    加入队伍
                </van-button>
            </van-form>
        </template>
    </van-popup>
</template>

<script setup lang="ts">
import {getUserId, setEditPageName} from "../config/globalParameter.js";
import MyAxios from "../plugins/myAxios.js";
import {showConfirmDialog, showNotify, showToast} from "vant";
import {useRouter} from "vue-router";
import {ref} from 'vue';
import {toastAccept, toastError} from "../plugins/toastUtils.js";

const router = useRouter();

interface TeamCardListProps {
    teamList: API.TeamUserVo[];
}

// @ts-ignore
const props = withDefaults(defineProps<TeamCardListProps>(), {
    teamList: [] as API.TeamUserVo[],
});

const showJoinTeamButton = (captainId, userList) => {
    if (captainId == getUserId()) {
        return false;
    } else {
        const userId = getUserId();
        for (let i = 0; i < userList.length; i++) {
            if (userList[i].id == userId) {
                return false;
            }
        }
        return true;
    }
}

const show = ref(false);
const joinTeamId = ref();

const joinTeam = async (teamId, teamStatus) => {
    if (teamStatus == 2) {
        joinTeamId.value = teamId;
        show.value = true;
    } else {
        await showConfirmDialog({
            message: '确认加入队伍吗?'
        }).then(async () => {
                const res = await MyAxios.post('/team/join', {teamId: teamId});
                // @ts-ignore
                if (res.code === 10000) {
                    toastAccept('加入队伍成功!');
                } else {
                    // @ts-ignore
                    toastError(res.description);
                }
            }
        ).catch(async () => {
            console.log('取消加入队伍');
        })

    }
}

const teamPassword = ref();

const joinEncryptedTeam = async () => {
    await showConfirmDialog({
        message: '确认加入队伍吗?'
    }).then(async () => {
            const res = await MyAxios.post('/team/join', {teamId: joinTeamId.value, teamPassword: teamPassword.value});
            // @ts-ignore
            if (res.code === 10000) {
                showNotify({
                    type: 'success',
                    message: '加入队伍成功!',
                    duration: 1000
                });
            } else {
                // @ts-ignore
                showNotify({
                    type: 'danger',
                    message: res.description,
                    duration: 1000
                });
            }
        }
    ).catch(async () => {
        console.log('取消加入队伍');
    })
}

const checkTeam = (teamId) => {
    setEditPageName('队伍');
    router.push({
        path: '/teamInfo',
        query: {
            teamId: teamId
        }
    });
}

const columns = [
    {name: '公共', value: 0},
    {name: '私有', value: 1},
    {name: '加密', value: 2},
];
</script>

<style scoped>

</style>