import {ref} from "vue";

const loginFlag = ref(false);
const currentUserAccount = ref('');
const currentUserId = ref(0);
const currentMainPageName = ref('云交友');
const editPageName = ref('');

const setLoginFlag = (flag) => {
    sessionStorage.setItem("loginFlag", flag);
    loginFlag.value = flag;
}

const getLoginFlag = () => {
    const storeLoginFlag = sessionStorage.getItem("loginFlag");
    return storeLoginFlag == null ? loginFlag.value : storeLoginFlag;
}

const setUserAccount = (userAccount) => {
    sessionStorage.setItem("userAccount", userAccount);
    currentUserAccount.value = userAccount;
}

const getUserAccount = () => {
    const storeUserAccount = sessionStorage.getItem("userAccount");
    return storeUserAccount == null ? currentUserAccount.value : storeUserAccount;
}

const setUserId = (userId) => {
    sessionStorage.setItem("userId", userId);
    currentUserId.value = userId;
}

const getUserId = () => {
    const storeUserId = sessionStorage.getItem("userId");
    return storeUserId == null ? currentUserId.value : storeUserId;
}

const setCurrentMainPageName = (pageName) => {
    sessionStorage.setItem("currentMainPageName", pageName);
    currentMainPageName.value = pageName;
}

const getCurrentMainPageName = () => {
    const storeMainPageName = sessionStorage.getItem("currentMainPageName")
    return storeMainPageName == null ? currentMainPageName.value : storeMainPageName;
}

const setEditPageName = (pageName) => {
    sessionStorage.setItem("editPageName", pageName);
    editPageName.value = pageName;
}

const getEditPageName = () => {
    const storeEditPageName = sessionStorage.getItem("editPageName")
    return storeEditPageName == null ? editPageName.value : storeEditPageName;
}

export {
    setLoginFlag,
    getLoginFlag,
    setUserAccount,
    getUserAccount,
    setUserId,
    getUserId,
    setCurrentMainPageName,
    getCurrentMainPageName,
    setEditPageName,
    getEditPageName,
};