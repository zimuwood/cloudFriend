import {showToast} from "vant";

const toastHint = (res) => {
    showToast({
        message: res,
        icon: 'smile',
    });
}

const toastAccept = (res) => {
    showToast({
        message: res,
        icon: 'success',
    });
}

const toastError = (res) => {
    showToast({
        message: res,
        icon: 'cross',
    });
}

const toastFail = (res) => {
    showToast({
        message: res,
        icon: 'fail',
    });
}

export {
    toastError,
    toastFail,
    toastAccept,
    toastHint,
}