<template>
    <van-form @submit="onSubmit">
        <van-field
                v-model="editUser.currentValue"
                :name="editUser.editKey.toString()"
                :label="editUser.editName.toString()"
                :placeholder="`请输入 ${editUser.editName}`"
                :rules="editRule"
        />
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                确认修改
            </van-button>
        </div>
    </van-form>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {onMounted, ref} from "vue";
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

const currentUser = ref();
const onSubmit = async () => {
    if (editUser.value.editKey === "profile" && editUser.value.currentValue === "") {
        editUser.value.currentValue = "这个人很懒，还没有写简介-_-!"
    }
    // @ts-ignore
    if (getLoginFlag()) {
        currentUser.value = getUserAccount();
    } else {
        // @ts-ignore
        toastFail('登录状态错误');
        await router.replace('/user');
    }

    const res = await myAxios.post('/user/update', {
            [editUser.value.editKey]: editUser.value.currentValue,
            'userAccount': currentUser.value,
        }, {
        params: {
            updateKey: editUser.value.editKey,
        }
    })
    // @ts-ignore
    if (res.code === 10000) {
        toastAccept( editUser.value.editName + '修改成功!');
    }
    else {
        // @ts-ignore
        toastError('出错了,修改失败...');
    }
    await router.replace('/user');
};

const editRule = ref();

const usernameRule = [
    {
        required: true,
        message: '昵称不能为空哦'
    },
    {
        pattern: new RegExp("^[\u4e00-\u9fa5a-zA-Z0-9]{1,16}$"),
        message: '昵称只能是4-16位汉字、数字以及字母'
    }
]

const profileRule = [
    {
        pattern: new RegExp("^.{0,50}$"),
        message: '简介限制的字数是50字'
    }
]

const phoneRule = [
    {
        pattern: new RegExp("^\\d{11}$"),
        message: '电话号码格式错误'
    }
]

const emailRule = [
    {
        pattern: new RegExp("\\w+@\\w+(\\.\\w+)+"),
        message: '邮箱格式错误'
    }
]

const getRule = () => {
    switch (editUser.value.editKey) {
        case 'username': editRule.value = usernameRule; break;
        case 'profile': editRule.value = profileRule; break;
        case 'phone': editRule.value = phoneRule; break;
        case 'email': editRule.value = emailRule; break;
    }
}

onMounted(async () =>{
    getRule();
})

</script>

<style scoped>

</style>