<template>
    <van-form @submit="onSubmit">
        <van-divider content-position="left">已选标签</van-divider>
        <div style="margin: 10px;">
            <van-tag :show="true" v-for="tag in activeIds" closeable size="medium" type="primary" @close="deleteTag(tag)" style="margin-right: 5px; margin-top: 3px;">
                {{ tag }}
            </van-tag>
        </div>

        <van-floating-panel>
            <van-cell-group title="全部标签" >
                <van-cell>
                    <van-tree-select
                        v-model:active-id="activeIds"
                        v-model:main-active-index="activeIndex"
                        :items="items"
                        max="20"
                    />
                </van-cell>
            </van-cell-group>
        </van-floating-panel>

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
import MyAxios from "../../plugins/myAxios.js";

const route = useRoute();
const router = useRouter();

const editUser = ref({
    editKey: route.query.editKey,
    editName: route.query.editName,
    currentValue: route.query.currentValue,
})

const activeIds = ref([]);
const activeIndex = ref(0);
// let items = [
//     {
//         text: '浙江',
//         children: [
//             { text: '杭州', id: '杭州' },
//             { text: '温州', id: '温州' },
//             { text: '宁波', id: '宁波' },
//         ],
//     },
//     {
//         text: '江苏',
//         children: [
//             { text: '南京', id: '南京' },
//             { text: '无锡', id: '无锡' },
//             { text: '徐州', id: '徐州' },
//             { text: '淮安', id: '淮安' },
//             { text: '镇江', id: '镇江' },
//             { text: '常州', id: '常州' },
//             { text: '苏州', id: '苏州' },
//             { text: '宿迁', id: '宿迁' },
//         ],
//     },
//     {
//         text: '编程语言',
//         children: [
//             { text: 'java', id: 'java' },
//             { text: 'python', id: 'python' },
//             { text: 'c++', id: 'c++' },
//             { text: 'go', id: 'go' },
//         ]
//     },
// ];
const items = ref();

const getTags = async () => {
    const res = await MyAxios.get('/user/getTags');
    // @ts-ignore
    if (res.code === 10000) {
        items.value = res.data;
    } else {
        // @ts-ignore
        toastError(res.description);
    }
}

// 移除标签
const deleteTag = (tagId) => {
    activeIds.value = activeIds.value.filter(item => {
        return item !== tagId;
    })
};

// @ts-ignore
const activeIdsInit = () => {
    // @ts-ignore
    activeIds.value = editUser.value.currentValue;
}

const currentUser = ref();
const onSubmit = async () => {
    // @ts-ignore
    if (getLoginFlag()) {
        currentUser.value = getUserAccount();
    } else {
        toastFail('登录状态错误');
        await router.replace('/user');
    }

    const res = await myAxios.post('/user/update', {
        [editUser.value.editKey]: activeIds.value.toString(),
        'userAccount': currentUser.value,
    }, {
        params: {
            updateKey: editUser.value.editKey,
        },
    })
    // @ts-ignore
    if (res.code === 10000) {
        toastAccept( editUser.value.editName + '修改成功!');
    }
    else {
        toastError('出错了,修改失败...');
    }
    await router.replace('/user');
};

onMounted(async () =>{
    await activeIdsInit();
    await getTags();
})

</script>

<style scoped>

</style>