<template>
    <div style="text-align: center">
        <van-image :src="cloudFriends" width="60%"/>
        <van-highlight :keywords="keywords" :source-string="text" style="color: #888888; margin-bottom: 10px;"/>
    </div>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field
                    v-model="userAccount"
                    name="账号"
                    label="账号"
                    placeholder="在这里填写账号^_^"
                    left-icon="contact"
                    clearable
                    :rules="[
                      {
                          required: true,
                          message: '没账号怎么登录?',
                      },
                      {
                          pattern: new RegExp('^\\d{10}$'),
                          message: '账号只能是10位且是纯数字',
                      }
                  ]"
            />
            <van-field
                    v-model="password"
                    :type="passwordType"
                    name="密码"
                    label="密码"
                    placeholder="在这里填写密码\^o^/"
                    clearable
                    left-icon="lock"
                    :right-icon="passwordShow ? 'eye-o' : 'closed-eye'"
                    :onClickRightIcon="onClickIcon"
                    :rules="[
                        {
                            required: true,
                            message: '密码都不填还想登录?'
                        },
                        {
                            pattern: new RegExp('^\\w{6,16}$'),
                            message: '密码不能包含特殊字符，且需要是6-16位',
                        }
                    ]"
            />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                登录
            </van-button>
        </div>
    </van-form>
    <div style="margin: 16px;">
        <van-button round block type="primary" @click="toRegister">
            注册
        </van-button>
    </div>
</template>

<script setup>
import {useRouter} from "vue-router";
import {onMounted, ref} from 'vue';
import myAxios from "../plugins/myAxios.js";
import {showNotify} from "vant";
import {setLoginFlag, getLoginFlag, setUserAccount, setUserId} from "../config/globalParameter.js"
import cloudFriends from "../assets/cloudFriends.jpg"

const router = useRouter();

const userAccount = ref();
const password = ref();
const onSubmit = async () => {
    const res = await myAxios.post('/user/login', {userAccount: userAccount.value, userPassword: password.value})
    if (res.code === 10000) {
        showNotify({
            type: 'success',
            message: '登录成功',
            duration: 1000
        })
        setLoginFlag(true);
        setUserAccount(userAccount.value);
        setUserId(res.data.id);
        await router.replace('/');
    } else {
        showNotify({
            type: 'danger',
            message: res.description,
            duration: 1000
        });
    }
}

const passwordShow = ref(false);
const passwordType = ref('password')
const onClickIcon = () => {
    passwordShow.value = !passwordShow.value;
    passwordType.value = passwordShow.value ? 'text' : 'password';
}

const text = "在云交友，找到志同道合的伙伴^_^/";
const keywords = ['云交友', '伙伴'];

const toRegister = () => {
    router.push('/register')
}

onMounted(async () => {
    if (getLoginFlag() === true) {
        await router.replace('/');
    }
})
</script>

<style scoped>

</style>