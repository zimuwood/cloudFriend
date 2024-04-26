<template>
    <van-nav-bar
            :title="pageName"
            left-arrow
            @click-left="onClickLeft"
            v-if="mountFlag"
    />

    <div id="content">
        <router-view  />
    </div>
</template>

<script setup>
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {getEditPageName} from "../config/globalParameter.js";

const router = useRouter();

const onClickLeft = () => {
    router.back();
};

const mountFlag = ref(false);
const pageName = ref('');

onMounted(async () => {
    mountFlag.value = false;
    pageName.value = getEditPageName();
    mountFlag.value = true;
})
</script>

<style scoped>
#content {
    padding-bottom: 60px;
}
</style>