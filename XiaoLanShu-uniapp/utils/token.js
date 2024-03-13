import {appConfig} from '../config/config.js'

export const tokenUtil = {
    set: (s) => {
        uni.setStorageSync(appConfig.tokenKey, s)
    },
    get: () => {
        return uni.getStorageSync(appConfig.tokenKey).token
    },
    getExpireTime(){
        return uni.getStorageSync(appConfig.tokenKey). expireDateTime
    },
    clear: () => {
        uni.removeStorageSync(appConfig.tokenKey)
    }
}