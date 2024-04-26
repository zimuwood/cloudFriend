<template>
    <div style="text-align: center">
        <van-image :src="cloudFriends" width="60%"/>
        <van-highlight :keywords="keywords" :source-string="text" style="color: #888888; margin-bottom: 10px;"/>
    </div>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field
                    v-model="userName"
                    name="昵称"
                    label="昵称"
                    placeholder="填写您的昵称"
                    left-icon="contact"
                    clearable
                    :rules="[
                      {
                          required: true,
                          message: '昵称不能为空',
                      },
                      {
                          pattern: new RegExp('^[\u4e00-\u9fa5a-zA-Z0-9]{1,16}$'),
                          message: '昵称只能是4-16位汉字、数字以及字母'
                      }
                  ]"
            />
            <van-field
                    v-model="password"
                    :type="passwordType"
                    name="密码"
                    label="密码"
                    placeholder="在这里填写密码"
                    clearable
                    left-icon="lock"
                    :right-icon="passwordShow ? 'eye-o' : 'closed-eye'"
                    :onClickRightIcon="onClickIcon"
                    :rules="[
                        {
                            required: true,
                            message: '密码很重要，不能为空哦'
                        },
                        {
                            pattern: new RegExp('^\\w{6,16}$'),
                            message: '密码不能包含特殊字符，且需要是6-16位',
                        }
                    ]"
            />
            <van-field
                v-model="passwordCheck"
                type="password"
                name="确认密码"
                label="确认密码"
                placeholder="请再次填写密码"
                clearable
                left-icon="lock"
                :onClickRightIcon="onClickIcon"
                :rules="[
                        {
                            required: true,
                            message: '请填写确认密码'
                        },
                        {
                            pattern: new RegExp('^\\w{6,16}$'),
                            message: '密码不能包含特殊字符，且需要是6-16位',
                        },
                        {
                            validator: (val) => { return val === password; },
                            message: '两次输入的密码不一致'
                        }
                    ]"
            />
            <van-field label="性别" left-icon="smile-o">
                <template #input>
                    <van-radio-group v-model="gender" direction="horizontal">
                        <van-radio name="0">男</van-radio>
                        <van-radio name="1">女</van-radio>
                    </van-radio-group>
                </template>
            </van-field>
        </van-cell-group>

        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                确认注册
            </van-button>
        </div>
        <div style="margin: 16px;">
            <van-button round block type="primary" @click="router.replace('/login')">
                返回登录
            </van-button>
        </div>

    </van-form>
</template>

<script setup>
import {useRouter} from "vue-router";
import {onMounted, ref} from 'vue';
import myAxios from "../plugins/myAxios.js";
import {showDialog, showNotify} from "vant";
import {getLoginFlag, setLoginFlag, setUserAccount, setUserId} from "../config/globalParameter.js"
import cloudFriends from "../assets/cloudFriends.jpg"

const router = useRouter();

const userName = ref();
const password = ref();
const passwordCheck = ref();
const gender = ref('0');
const onSubmit = async () => {
    const res = await myAxios.post('/user/register', { userName: userName.value, userPassword: password.value, checkPassword: passwordCheck.value, gender: gender === '0' ? 0 : 1 })
    if (res.code === 10000) {
        showNotify({
            type: 'success',
            message: '注册成功，即将自动登录',
            duration: 1000
        })
        showDialog({
            title: '账号提示',
            message: '账号是登录的唯一凭证，请一定要记住账号',
        }).then(() => {
            showDialog({
                message: '您的账号是: ' + res.data.userAccount,
            })
        });
        await toLogin(res.data);
    } else {
        showNotify({
            type: 'danger',
            message: res.description,
            duration: 1000
        });
    }
}

const toLogin = async (user) => {
    const res = await myAxios.post('/user/login', {userAccount: user.userAccount, userPassword: password.value})
    if (res.code === 10000) {
        showNotify({
            type: 'success',
            message: '登录成功，首次登录请记住账号',
            duration: 1000
        })
        setLoginFlag(true);
        setUserAccount(user.userAccount);
        setUserId(user.id);
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

const text = "欢迎注册云交友 *^_^*";
const keywords = ['云交友'];


onMounted(async () => {
    if (getLoginFlag() === true) {
        await router.replace('/');
    }
})
</script>

<style scoped>

</style>