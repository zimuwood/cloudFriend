<template>
    <van-form @submit="onSubmit">
        <van-divider>原头像</van-divider>
        <div style="text-align: center">
            <van-image height="150px" :src="editUser.currentValue" @click="previewAvatar" >
                <template #loading>
                    <van-loading type="spinner" size="20" />
                </template>
            </van-image>
        </div>
        <van-divider>上传新头像(长宽比1:1的更美观)</van-divider>
        <div style="text-align: center">
            <van-uploader v-model="avatar" max-size="200px" :max-count="1" preview-size="150" :before-read="beforeRead" :after-read="afterRead" :delete="deleteImg"/>
        </div>
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
import {showImagePreview} from "vant";
import myAxios from "../../plugins/myAxios.js";
import {getLoginFlag, getUserAccount} from "../../config/globalParameter.js";
import {toastAccept, toastError, toastFail, toastHint} from "../../plugins/toastUtils.js";

const route = useRoute();
const router = useRouter();
const editUser = ref({
    editKey: route.query.editKey,
    editName: route.query.editName,
    currentValue: route.query.currentValue,
})

const avatar = ref([]);

const previewAvatar = () => showImagePreview({
    images: [editUser.value.currentValue.toString()],
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
const formData = new FormData();
const afterRead = (file) => {
    formData.append('image', file.file);
}
const deleteImg = () => {
    formData.delete('image');
}

const currentUser = ref();
const onSubmit = async () => {
    if (getLoginFlag()) {
        currentUser.value = getUserAccount();
    } else {
        // @ts-ignore
        toastFail('登录状态错误');
        await router.replace('/user');
    }

    if (!formData.get('image')) {
        toastHint("请先上传新头像再点击提交!")
        return;
    }
    const uploadRes: API.Response<string> = await myAxios.post('/user/getAvatar', formData);
    if (uploadRes.code === 10000) {
        editUser.value.currentValue = uploadRes.data;
    } else {
        // @ts-ignore
        toastError('头像上传失败');
        return;
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