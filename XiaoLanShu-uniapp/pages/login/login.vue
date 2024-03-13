<template>
	<view class="container">
		<view class="title">登录</view>
		<view class="info">
			<view class="center">
				<input placeholder="输入手机号/邮箱" class="info-input" v-model="userInfo.username" />
				<input placeholder="输入密码" class="info-input" type="password" v-model="userInfo.password" />
				<!-- <input placeholder="APIIP配置" class="info-input" v-model="SERVER_IP" /> -->
			</view>
			<view class="info-bottom">
				<view @click="loginByCode">验证码登录</view>
				<view class="info_right">
					<view @click="findPassword">忘记密码？</view>
					<view @click="regist">注册</view>
				</view>

			</view>
			<view class="login">
				<button @click='login'>登录</button>
			</view>
		</view>
		<view class="fotter">
			<tui-divider width="80%">其他方式登录</tui-divider>
			<view class="other-login">
				<view class="other-item">
					<view class="icon">
						<image src="/static/images/share/icon_qq.png" data-loginType="qq" @click="auth"></image>
					</view>
					<view class="font">QQ</view>
				</view>

				<view class="other-item">
					<view class="icon">
						<image src="/static/images/share/icon_wechat.png" data-loginType="weixin" @click="auth"></image>
					</view>
					<view class="font">微信</view>
				</view>

				<view class="other-item">
					<view class="icon">
						<image src="/static/images/share/icon_sina.png" @click="auth"></image>
					</view>
					<view class="font">微博</view>
				</view>

			</view>
		</view>
	</view>
</template>

<script>
	import {
		// login,
		otherLogin,
	} from '@/api/login.js';
	import {
		tokenUtil,
	} from '@/utils/token.js';
	import {
		loginByPwd,
	} from '@/api/v1/login.js';
	import socket from 'plus-websocket';
	import {
		appConfig,
		SERVER_IP
	} from '@/config/config.js';

	export default {
		data() {
			return {
				userInfo: {},
				// ip: appConfig.SERVER_IP,
			};
		},

		created() {
			uni.hideTabBar();
		},

		methods: {
			login() {

				if (this.userInfo.username == null || this.userInfo.password == null) {
					uni.showToast({
						title: '信息输入有误',
						icon: 'error',
					});
				}
				loginByPwd(this.userInfo.username, this.userInfo.password).then(
					res => {
						if (res.success) {
							let userBasic = res.data.userBasic;
							tokenUtil.set(res.data.AuthHeader);
							uni.setStorageSync("userBasic", userBasic);
							//跳转到主页
							setTimeout(() => {
								uni.reLaunch({
									url: '/pages/index/index',
								});
							}, 500);
						} else {
							uni.showToast({
								title: res.msg,
								icon: 'error',
							});
						}
					},
				).catch(err => {

				});


				// else {
				// 	login(this.userInfo).then(res => {
				// 		if (res.data.res == 0) {
				// 			uni.showToast({
				// 				title: res.data.message
				// 			})
				// 		} else {
				// 			let user = res.data.userInfo
				// 			console.log("---user",user)
				// 			tokenUtil.set(res.data.Jwt_token)
				// 			uni.setStorageSync("userInfo", user)
				//
				// setTimeout(() => {
				// 	uni.reLaunch({
				// 		url: "/pages/index/index"
				// 	});
				// }, 500)
				// 		}
				//
				// 	})
				// }


			},
			regist() {
				uni.navigateTo({
					url: '/pages/regist/regist',
				});
			},
			loginByCode() {
				uni.navigateTo({
					url: '/pages/login/loginByCode',
				});
			},
			findPassword() {
				uni.navigateTo({
					url: '/pages/login/findPassword',
				});
			},

			//第三方授权登录 未实现
			auth(e) {
				//一定要用that
				let that = this;

				let loginType = e.currentTarget.dataset.logintype;

				uni.login({
					provider: loginType,
					success(res) {

						// 登录成功后, 获取用户数据
						uni.getUserInfo({
							provider: loginType,
							success(info) {
								var userInfo = info.userInfo;
								var nickName = '';
								var avatarUrl = '';
								var openId = '';
								if (loginType == 'weixin') {
									nickName = userInfo.nickName;
									avatarUrl = userInfo.avatarUrl;
									openId = userInfo.openId;
								} else if (loginType == 'qq') {
									nickName = userInfo.nickname;
									avatarUrl = userInfo.figureurl_qq_2;
									// qq返回了多个尺寸的头像, 按需选择
									openId = userInfo.openId;
								} else if (loginType == 'sinaweibo') {
									nickName = userInfo.nickname;
									avatarUrl = userInfo.avatar_large;
									openId = userInfo.id;
								}

								let userOtherLoginRelationDTO = {};
								userOtherLoginRelationDTO.otherUserId = openId;
								userOtherLoginRelationDTO.otherUsername = nickName;
								userOtherLoginRelationDTO.otherAvatar = avatarUrl;
								userOtherLoginRelationDTO.otherToken = res.access_token;

								otherLogin(userOtherLoginRelationDTO).then(res => {

									let user = res.data.userInfo;
									tokenUtil.set(res.data.Jwt_token);
									uni.setStorageSync('userInfo', user);

									setTimeout(() => {
										uni.reLaunch({
											url: '/pages/index/index',
										});
									}, 500);

								});

							},
						});
					},
				});
			},
		},
	};
</script>

<style scoped>
	@import url(./css/login.css);
</style>