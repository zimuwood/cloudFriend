<template>
    <div v-if="isMount">
        <van-form @submit="onSubmit">
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
                    <template #right-icon>
                        <van-uploader v-model="avatar" :max-count="1" preview-size="50" :before-read="beforeRead"
                                      :after-read="afterRead" :delete="deleteImg"/>
                    </template>
                </van-field>
                <van-field
                        v-model="teamInfo.teamName"
                        label="队伍名称"
                        placeholder="请输入队伍名称"
                        :rules="[
                                {
                                    validator: val => { return val.length <= 20 },
                                    message: '队伍名称应该在 20 字以内',
                                },
                                {
                                    required: true,
                                    message: '队伍名称是必填的!'
                                }
                            ]"
                />
                <van-field
                        v-model="teamInfo.teamDescription"
                        label="队伍描述"
                        placeholder="介绍一下自己的队伍吧"
                        maxlength="100"
                        show-word-limit
                        :rules="[
                                {
                                    validator: val => { return val.length <= 100 },
                                    message: '队伍名称应该在 100 字以内',
                                }
                            ]"
                />
                <van-field
                        v-model="teamInfo.maxNum"
                        label="队伍最大人数"
                        type="number"
                        placeholder="请输入队伍最大人数"
                        :rules="[
                                {
                                    validator: val => { return val >= 1 && val <= 15 },
                                    message: '请输入1~15的整数!',
                                }
                            ]"
                />
                <van-field
                        v-model="teamState"
                        label="队伍状态"
                        readonly
                        is-link
                        @click="show = true"
                />
                <div :hidden="teamInfo.teamStatus !== 2">
                    <van-field
                            v-model="teamInfo.teamPassword"
                            label="入队密码"
                            placeholder="请填写私密队伍入队密码"
                            :rules="[
                            {
                            pattern: new RegExp('^\\w{6,12}$'),
                            message: '密码不能包含特殊字符，且需要是6-12位'
                            },
                        ]"
                    />
                </div>
                <van-field
                        v-model="teamExpireTime"
                        is-link
                        readonly
                        name="calendar"
                        label="队伍过期时间"
                        placeholder="点击选择过期时间"
                        @click="showCalendar = true"
                        :rules="[
                                    {
                                        validator: (val) => {
                                            const now = new Date();
                                            let year = now.getFullYear().toString();
                                            let month = now.getMonth() + 1;
                                            let day = now.getDate();
                                            return val === '' || new Date(val) >= new Date(year + '-' + month + '-' + day)
                                        },
                                        message: '过期时间必须在今天以后'
                                    },
                                    {
                                        required: true,
                                        message: '请选择过期时间!'
                                    }
                                ]"
                />
            </van-cell-group>
            <van-button round block type="primary" native-type="submit">
                确认修改
            </van-button>
        </van-form>
        <van-calendar v-model:show="showCalendar" @confirm="onConfirm"/>
        <van-action-sheet v-model:show="show" :actions="columns" @select="onSelect" close-on-click-action/>
    </div>
</template>

<script setup>
import {useRoute, useRouter} from "vue-router";
import {ref, onMounted} from 'vue';
import {setEditPageName} from "../config/globalParameter.js";
import MyAxios from "../plugins/myAxios.js";
import {showImagePreview} from "vant";
import myAxios from "../plugins/myAxios.js";
import {toastAccept, toastError, toastHint} from "../plugins/toastUtils.js";

const route = useRoute();
const router = useRouter();
const isMount = ref(false)

const teamInfo = ref();
const teamState = ref('公共');
const teamUser = ref();
const teamExpireTime = ref();

onMounted(async () => {
    setEditPageName('队伍信息');
    const {teamId} = route.query;
    const res = await MyAxios.get('/team/get', {params: {teamId: teamId}});
    if (res.code === 10000) {
        teamInfo.value = res.data;
        const status = teamInfo.value.teamStatus;
        teamState.value = status === 0 ? '公共' : status === 1 ? '私有' : '加密';
        teamUser.value = teamInfo.value.currentNum + '/' + teamInfo.value.maxNum;
        teamExpireTime.value = teamInfo.value.expireTime.toString().substring(0, 10);
        isMount.value = true;
    } else {
        toastError(res.description);
        await router.replace('/');
    }
})

const previewAvatar = () => showImagePreview({
    images: [teamInfo.value.teamAvatar.toString()],
    closeable: true,
});

const avatarTypes = ['image/jpg', 'image/jpeg', 'image/webp', 'image/png']
const beforeRead = (file) => {
    if (!avatarTypes.includes(file.type)) {
        toastHint('只支持jpg,jpeg,png,webp格式的图片文件哦');
        return false;
    }
    return true;
};

const avatar = ref();
const formData = new FormData();
const afterRead = (file) => {
    formData.append('image', file.file);
}
const deleteImg = () => {
    formData.delete('image');
}

const showCalendar = ref(false);
const onConfirm = (date) => {
    teamInfo.value.expireTime = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
    showCalendar.value = false;
}

const show = ref(false);

const columns = [
    {name: '公共', value: 0},
    {name: '私有', value: 1},
    {name: '加密', value: 2},
];

const onSelect = (item) => {
    teamState.value = item.name;
    teamInfo.value.teamStatus = item.value;
    show.value = false;
};

const onSubmit = async () => {
    if (formData.get('image')) {
        const uploadRes = await myAxios.post('/team/getAvatar', formData);
        if (uploadRes.code === 10000) {
            teamInfo.value.teamAvatar = uploadRes.data;
        } else {
            toastError("头像上传失败");
            return;
        }
    }

    const res = await MyAxios.post('/team/update', {
        id: teamInfo.value.id,
        teamAvatar: teamInfo.value.teamAvatar,
        teamName: teamInfo.value.teamName,
        teamDescription: teamInfo.value.teamDescription,
        maxNum: teamInfo.value.maxNum,
        expireTime: teamInfo.value.expireTime,
        teamStatus: teamInfo.value.teamStatus,
        teamPassword: teamInfo.value.teamPassword
    });
    if (res.code === 10000) {
        toastAccept('修改队伍信息成功!');
    } else {
        // @ts-ignore
        toastError(res.description);
    }
    router.back();
}


</script>

<style scoped>

</style>