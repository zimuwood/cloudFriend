<template>
    <van-form @submit="onSubmit">
        <van-field
            v-model="fieldValue"
            is-link
            readonly
            label="性别"
            placeholder="选择性别"
            @click="showPicker = true"
        />
        <van-popup v-model:show="showPicker" round position="bottom">
            <van-picker
                :columns="columns"
                @cancel="showPicker = false"
                @confirm="onConfirm"
            />
        </van-popup>
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                确认修改
            </van-button>
        </div>
    </van-form>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../../plugins/myAxios.js";
import {getLoginFlag, getUserAccount} from "../../config/globalParameter.js";
import {toastAccept, toastError, toastFail} from "../../plugins/toastUtils.js";

const route = useRoute();
const router = useRouter();

const editUser = ref({
    editKey: route.query.editKey,
    editName: route.query.editName,
    currentValue: route.query.currentValue,
})

const columns = [
    { text: '男', value: 0 },
    { text: '女', value: 1 },
];

const fieldValue = ref(columns[editUser.value.currentValue].text);
const showPicker = ref(false);

const onConfirm = ({ selectedOptions }) => {
    showPicker.value = false;
    fieldValue.value = selectedOptions[0].text;
    editUser.value.currentValue = (fieldValue.value === '男' ? 0 : 1).toString();
};

const currentUser = ref();
const onSubmit = async () => {
    if (getLoginFlag()) {
        currentUser.value = getUserAccount();
    } else {
        toastFail('用户信息错误');
        await router.replace('/user');
    }

    const res: API.Response<API.UserType> = await myAxios.post('/user/update', {
        [editUser.value.editKey]: editUser.value.currentValue,
        'userAccount': currentUser.value,
    }, {
        params: {
            updateKey: editUser.value.editKey,
        }
    })

    if (res.code === 10000) {
        toastAccept( editUser.value.editName + '修改成功!');
    }
    else {
        toastError('出错了,修改失败...');
    }
    await router.replace('/user');
};

</script>

<style scoped>

</style>