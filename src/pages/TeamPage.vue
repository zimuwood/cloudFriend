<template>
    <van-tabs animated v-model:active="pageNum" @change="pageChange">
        <van-tab title="我的队伍" name="1">
            <div>
                <team-card-list :team-list="teamList"/>
            </div>
        </van-tab>
        <van-tab title="搜索队伍" name="2">
            <div>
                <van-search
                        v-model="searchText"
                        left-icon="search"
                        placeholder="请输入要搜索的队伍信息"
                        show-action
                >
                    <template #action>
                        <van-button type="primary" loading-type="spinner" @click="selectTeams" size="small"
                                    style="margin: 10px 0;">搜索
                        </van-button>
                    </template>
                </van-search>
                <div style="margin: 0 12px;border-radius: 3px">
                    <div style="text-align: right;">
                        <van-icon color="#1989FA" :name=arrowDir @click="changeArrowDir">条件搜索</van-icon>
                    </div>
                    <div :hidden="hideDetail">
                        <van-form @submit="onSubmit">
                            <van-field
                                    v-model="teamId"
                                    label="队伍id"
                                    type="number"
                                    placeholder="请输入要搜索的队伍id"
                                    :rules="[
                                {
                                    validator: val => { return val === '' || val > 0 },
                                    message: '请输入正确的队伍id',
                                }
                            ]"
                            />
                            <van-field
                                    v-model="maxNum"
                                    label="队伍最大人数"
                                    type="number"
                                    placeholder="请输入要搜索的队伍最大人数"
                                    :rules="[
                                {
                                    validator: (val) => { return val === '' || (val >= 1 && val <= 20) },
                                    message: '队伍人数必须在 1~20 之间'
                                }
                            ]"
                            />
                            <van-field
                                    v-model="expireTime"
                                    is-link
                                    readonly
                                    name="calendar"
                                    label="队伍过期时间"
                                    placeholder="点击选择过期时间"
                                    @click="showCalendarSearch = true"
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
                        </van-form>
                    </div>
                </div>
                <div>
                    <team-card-list :team-list="teamList"/>
                </div>
                <van-pagination v-model="currentPage" :total-items="pages" :items-per-page="10"
                                v-if="teamList.length > 0"/>
            </div>
        </van-tab>
        <van-tab title="创建队伍" name="3">
            <van-form @submit="onSubmit">
                <van-cell-group inset style="margin: 10px 10px;">
                    <van-field
                            label="队伍头像"
                            readonly
                    >
                        <template #right-icon>
                            <van-uploader v-model="avatar" :max-count="1" preview-size="50" :before-read="beforeRead"
                                          :after-read="afterRead" :delete="deleteImg" />
                        </template>
                    </van-field>
                    <van-field
                            v-model="createTeamData.teamName"
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
                            v-model="createTeamData.teamDescription"
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
                            v-model="createTeamData.maxNum"
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
                            v-model="fieldValue"
                            label="队伍状态"
                            readonly
                            is-link
                            @click="show = true"
                    />
                    <div :hidden="createTeamData.teamStatus !== 2" >
                        <van-field
                            v-model="createTeamData.teamPassword"
                            label="入队密码"
                            placeholder="请填写私密队伍入队密码"
                            :rules="[
                                {
                                required: createTeamData.teamStatus === 2,
                                message: '加密队伍需要设置密码'
                                },
                                {
                                    pattern: createTeamData.teamStatus === 2 ? new RegExp('^\\w{6,12}$') : null,
                                    message: '密码不能包含特殊字符，且需要是6-12位'
                                }
                            ]"
                        />
                    </div>
                    <van-field
                            v-model="createTeamData.expireTime"
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
                <van-button round block type="primary" native-type="submit" >
                    创建队伍
                </van-button>
            </van-form>
        </van-tab>
    </van-tabs>
    <van-calendar v-model:show="showCalendarSearch" @confirm="onConfirmSearch"/>
    <van-calendar v-model:show="showCalendar" @confirm="onConfirm"/>
    <van-action-sheet v-model:show="show" :actions="columns" @select="onSelect" close-on-click-action/>
    <van-empty image="search" description="未找到满足条件的队伍" v-if="teamList.length === 0"/>
</template>

<script setup>
import {onMounted, ref} from "vue";
import MyAxios from "../plugins/myAxios.js";
import TeamCardList from "../components/TeamCardList.vue";
import {showNotify} from "vant";
import myAxios from "../plugins/myAxios.js";
import {toastError, toastFail, toastHint} from "../plugins/toastUtils.js";
import {getLoginFlag} from "../config/globalParameter.js";

const searchText = ref();
const teamId = ref();
const maxNum = ref();
const expireTime = ref();

const pageNum = ref('1');
const pageChange = () => {
    pageNum.value === '1' ? getMyTeams() : selectTeams();
}

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

const arrowDir = ref("arrow-up");
const hideDetail = ref(true);
const changeArrowDir = () => {
    hideDetail.value = !hideDetail.value;
    arrowDir.value = arrowDir.value === "arrow-up" ? "arrow-down" : "arrow-up";
}

const teamList = ref([]);
const getMyTeams = async () => {
    const res = await MyAxios.get('/team/getMyTeams');
    if (res.code === 10000) {
        teamList.value = res.data;
    } else {
        toastError(res.description);
    }
}

const showCalendarSearch = ref(false);
const onConfirmSearch = (date) => {
    expireTime.value = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
    showCalendarSearch.value = false;
}

const currentPage = ref(1);
const pages = ref(1);
const selectTeams = async () => {
    const res = await MyAxios.get('/team/search', {
        params: {
            id: teamId.value,
            teamInfo: searchText.value,
            maxNum: maxNum.value,
            expireTime: expireTime.value,
            currentPage: currentPage.value
        }
    });
    if (res.code === 10000) {
        teamList.value = res.data.records;
        pages.value = res.data.pages + 1;
    } else {
        toastError(res.description);
    }
}

const initTeamInfo = {
    "teamAvatar": "",
    "teamName": "",
    "teamDescription": "",
    "maxNum": "",
    "teamStatus": 0,
    "teamPassword": "",
    "expireTime": ""
}

const createTeamData = ref({...initTeamInfo});

const columns = [
    {name: '公共', value: 0},
    {name: '私有', value: 1},
    {name: '加密', value: 2},
];
const show = ref(false);
const fieldValue = ref(columns[createTeamData.value.teamStatus].name);
const onSelect = (item) => {
    fieldValue.value = item.name;
    createTeamData.value.teamStatus = item.value;
    show.value = false;
};

const showCalendar = ref(false);
const onConfirm = (date) => {
    createTeamData.value.expireTime = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
    showCalendar.value = false;
}

const onSubmit = async () => {
    if (formData.get('image')) {
        const uploadRes = await myAxios.post('/team/getAvatar', formData);
        if (uploadRes.code === 10000) {
            createTeamData.value.teamAvatar = uploadRes.data;
        } else {
            toastFail("头像上传失败");
            return;
        }
    }

    const res = await MyAxios.post('/team/create',
        {
            teamAvatar: createTeamData.value.teamAvatar,
            teamName: createTeamData.value.teamName,
            teamDescription: createTeamData.value.teamDescription,
            maxNum: createTeamData.value.maxNum,
            expireTime: new Date(createTeamData.value.expireTime),
            teamStatus: createTeamData.value.teamStatus,
            teamPassword: createTeamData.value.teamPassword
        }
    );
    if (res.code === 10000) {
        showNotify({
            type: 'success',
            message: '创建队伍成功',
            duration: 1000
        });
    } else {
        showNotify({
            type: 'danger',
            message: res.description,
            duration: 1000
        });
    }

    pageNum.value = '1';
}

onMounted(() => {
    if (!getLoginFlag()) {
        toastFail('登录状态错误');
    } else {
        getMyTeams();
    }
})

</script>

<style scoped>

</style>