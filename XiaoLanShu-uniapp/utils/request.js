import {
	appConfig
} from '../config/config.js'
import {
	tokenUtil
} from './token'


async function checkToken() {
	const tokenExpireDate = tokenUtil.getExpireTime();
	// console.log(tokenUtil.get())
	if (tokenUtil.get()) {
		// 计算两个日期之间的差值（以毫秒为单位）
		let differenceInMilliseconds = tokenExpireDate - new Date();
		// console.log('toekn还有'+differenceInMilliseconds+'分钟')
		// 转换为分钟
		let minutesUntil = Math.ceil(differenceInMilliseconds / (1000 * 60));
		if (minutesUntil <= 60) {
			//   TODO 更新TOKEN
		}
	}
}

const send = (url, data = {}, method = 'POST', showLoading = true) => {


	checkToken(); //TODO 校验所请求的是不是登录或者注册
	return new Promise((resolve) => {
		uni.request({
			method: method,
			url: url,
			data: data,
			header: (() => {
				const tokeValue = tokenUtil.get()
				let config = {
					'Content-Type': 'application/json'
				}

				if (tokeValue) {
					config[appConfig.tokenKey] = tokeValue
				}
				return config
			})(),
			success: (res) => {
				uni.hideLoading()

				if (res.data.code == 401) {
					uni.showToast({
						icon: "none",
						title: "用户未认证或登录过期,请重新登录"
					});
					uni.removeStorageSync("userInfo")
					tokenUtil.clear()
					setTimeout(function() {
						uni.reLaunch({
							url: "/pages/login/login"
						});
					}, 1000);
				}

				//如果时间小于1小时，则刷新token
				// if(jetLag<1&&!isRefresh){
				// 	isRefresh = true
				// 	let userInfo = uni.getStorageSync("userInfo")
				// 	  uni.request({
				// 		  method: 'post',
				// 		  url: appConfig.WEB_API+"/auth/auth/refreshToken",
				// 		  data: userInfo,
				// 		  header: (() => {
				// 		      let config = {
				// 		  		'Content-Type': 'application/json'
				// 		      }
				// 		      return config
				// 		  })(),
				// 		   success: (response) => {
				// 			   let refreshToken = response.data.data.Jwt_token
				// 			   uni.setStorageSync("expiration", response.data.data.expiration)
				// 			   tokenUtil.set(refreshToken)
				// 			   resolve(res)
				// 		   }
				// 	  })
				// }
				//}
				resolve(res.data)
			}
		})
	})
}

export const request = {
	get: (url, data) => {
		return send(url, data, 'GET')
	},
	post: (url, data) => {
		return send(url, data, 'POST')
	}
}