<template>
    <van-tabs animated>
        <van-tab title="标签搜索">
            <div>
                <van-search readonly left-icon="">
                    <template v-slot:label>
                        <van-icon name="search" size="18px" color="#9A9B9D"></van-icon>
                        <span v-if="activeIds.length === 0" style="color: #C8C9CC;">&nbsp;请选择要搜索的用户标签</span>
                        <van-tag v-for="tag in activeIds" color="#9B9C9D" :show="true" closeable size="medium"
                                 style="margin: 1px 1px 2px;" type="primary" @close="deleteTag(tag)">
                            {{ tag }}
                        </van-tag>
                    </template>
                </van-search>

                <van-divider content-position="left">可选标签</van-divider>
                <div style="text-align: center;">
                    <van-tree-select
                        v-model:active-id="activeIds"
                        v-model:main-active-index="activeIndex"
                        :items="items"
                        @click-item="checkNum"
                        max="10"
                    />
                    <van-button type="primary" loading-type="spinner" round @click="searchTags" size="large"
                                style="margin: 10px 0;">搜索
                    </van-button>
                </div>
            </div>
        </van-tab>
        <van-tab title="用户名或账号搜索">
            <div style="margin-bottom: 95%">
                <van-search
                    v-model="searchText"
                    left-icon="search"
                    placeholder="请输入要搜索的用户名或账号"
                />
            </div>
                <van-button type="primary" loading-type="spinner" round @click="searchInfo" size="large"
                                style="margin: 10px 0;">搜索
                </van-button>
        </van-tab>
    </van-tabs>

</template>

<script setup>
import 'vant/es/notify/style'
import {onMounted, ref} from 'vue';
import {showNotify} from 'vant';
import {useRouter} from "vue-router";
import MyAxios from "../plugins/myAxios.js";
import {toastError} from "../plugins/toastUtils.js";

let numFlag = false;
const router = useRouter();

// 已选中的标签
const activeIds = ref([]);
const activeIndex = ref(0);
// const items = [
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

const checkNum = () => {
    if (activeIds.value.length === 10 && numFlag) {
        showNotify({
            type: 'primary',
            message: '最多选择10个标签',
            duration: 1000
        });
    } else numFlag = activeIds.value.length === 10;
}

// 搜索标签
const searchTags = () => {
    if (activeIds.value.length === 0) {
        showNotify("最少需要选择一个标签!");
    } else {
        router.push({path: "/user/list/1", query: {tags: activeIds.value}});
    }
}

const searchText = ref();
// 搜索个人信息
const searchInfo = () => {
    if (searchText.value === "") {
        showNotify("请输入搜索内容!");
    } else {
        router.push({path: "/user/list/2", query: {searchText: searchText.value}});
    }
}

onMounted(async () =>{
    await getTags();
})
</script>

<style scoped>

</style>