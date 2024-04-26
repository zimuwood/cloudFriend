<template>
    <van-tabs animated v-model:active="pageNum" @change="changePage">
        <van-tab title="创建父标签" name="1">
            <van-form v-model="createParentTag" @submit="onSubmitParent">
                <van-cell-group inset style="margin: 10px 10px;">
                    <van-field
                            v-model="createParentTag.tagName"
                            label="标签名称"
                            placeholder="请输入创建的标签名称"
                            :rules="[
                                {
                                    validator: val => { return val.length <= 10 },
                                    message: '标签名称应该在 10 字以内',
                                },
                                {
                                    required: true,
                                    message: '标签名称是必填的!'
                                },
                                {
                                    validator: (value) => { return !parentTags.some(val => val.text === value) },
                                    message: '该父标签已存在!'
                                }
                            ]"
                    />
                </van-cell-group>
                <van-button round block type="primary" native-type="submit">
                    创建标签
                </van-button>
            </van-form>
        </van-tab>
        <van-tab title="创建子标签" name="2" style="height: 75vh;">
            <van-form v-model="createChildTag" @submit="onSubmitChild">
                <van-cell-group inset style="margin: 10px 10px;">
                    <van-field
                            v-model="parentTagName"
                            is-link
                            readonly
                            name="picker"
                            label="选择父标签"
                            placeholder="点击选择父标签"
                            @click="showPicker = true"
                            :rules="[
                                {
                                    required: true,
                                    message: '父标签是必选的!'
                                },
                            ]"
                    />
                    <van-popup v-model:show="showPicker" position="bottom" @cancel="showPicker = false" style="width: 50%; margin-left: 50%">
                        <van-picker
                                :columns="parentTags"
                                @confirm="onConfirm"
                                @cancel="showPicker = false"
                        />
                    </van-popup>
                    <van-field
                            v-model="createChildTag.tagName"
                            label="标签名称"
                            placeholder="请输入创建的标签名称"
                            :rules="[
                                {
                                    validator: val => { return val.length <= 10 },
                                    message: '标签名称应该在 10 字以内',
                                },
                                {
                                    required: true,
                                    message: '标签名称是必填的!'
                                },
                                {
                                    validator: (value) => { return !childrenTag.some(val => val.text === value) },
                                    message: '父标签中已存在该子标签!'
                                }
                            ]"
                    />
                </van-cell-group>
                <van-button round block type="primary" native-type="submit">
                    创建标签
                </van-button>
            </van-form>
        </van-tab>
    </van-tabs>
</template>

<script setup>
import {onMounted, ref} from "vue";
import MyAxios from "../plugins/myAxios.js";
import {toastAccept, toastError} from "../plugins/toastUtils.js";
import myAxios from "../plugins/myAxios.js";
import {useRouter} from "vue-router";

const router = useRouter();

const pageNum = ref('1');

const initTagInfo = {
    "tagName": "",
    "parentId": "",
    "isParent": "",
}
const createParentTag = ref({...initTagInfo});
const createChildTag = ref({...initTagInfo});

const parentTagName = ref();
const showPicker = ref(false);
const columns = ref();
const parentTags = ref([]);
const childrenTag = ref([]);

const getTags = async () => {
    const res = await MyAxios.get('/user/getTags');
    // @ts-ignore
    if (res.code === 10000) {
        columns.value = res.data;
        columns.value.forEach(val => { parentTags.value.push({text: val.text, tagId: val.tagId, value: val.text}) });
    } else {
        // @ts-ignore
        toastError(res.description);
    }
}

const onConfirm = ({ selectedOptions }) => {
    parentTagName.value = selectedOptions[0]?.text;
    createChildTag.value.parentId = selectedOptions[0]?.tagId;
    childrenTag.value = columns.value.find(val => val.tagId === createChildTag.value.parentId).children;
    showPicker.value = false;
};

const changePage = () => {
    showPicker.value = false;
}

const onSubmitParent = async () => {
    createParentTag.value.isParent = 1;
    const res = await myAxios.post('/user/createTag', {
        tagName: createParentTag.value.tagName,
        isParent: createParentTag.value.isParent,
    })

    if (res.code === 10000) {
        toastAccept('标签添加成功!即将跳转到首页。');
    } else {
        toastError('标签添加失败，请联系管理员。');
    }
    setTimeout(await router.replace('/'), 2000);

}

const onSubmitChild = async () => {
    createChildTag.value.isParent = 0;
    const res = await myAxios.post('/user/createTag', {
        tagName: createChildTag.value.tagName,
        parentId: createChildTag.value.parentId,
        isParent: createChildTag.value.isParent,
    })

    if (res.code === 10000) {
        toastAccept('标签添加成功!即将跳转到首页。');
    } else {
        toastError('标签添加失败，请联系管理员。');
    }

    setTimeout(await router.replace('/'), 2000);
}

onMounted(async () => {
    await getTags();
})

</script>

<style scoped>

</style>