import axios from "axios";

// 创建实例时配置默认值
const myAxios = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL,
    withCredentials: true,
});

export default myAxios;

// 添加请求拦截器
myAxios.interceptors.request.use(function (config) {
    console.log('我要发请求啦', config);
    // 在发送请求之前做些什么
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
myAxios.interceptors.response.use(function (response) {
    console.log('我收到你的请求啦', response);
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    return response.data;
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
});