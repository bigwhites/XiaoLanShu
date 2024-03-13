<template>
	<view class="container">
		<view class="top">
			<view class="title">注册</view>
		</view>
		<view class="main">
			<view class="center">
				<input placeholder="输入手机号/邮箱注册" class="info-input" v-model="value" />
				<view class="code">
					<input placeholder="验证码输入" class="info-input" v-model="code" />
					<tui-button type="white" width="200rpx" :link="true" height="60rpx" @click="getCode()" v-if="T">验证码
					</tui-button>
					<tui-tag type="white" v-else>{{ count }}</tui-tag>
				</view>
				<input type="password" placeholder="输入密码" class="info-input" v-model="pwd" />
				<input type="password" placeholder="再次输入密码" class="info-input" v-model="pwdRe" />
			</view>

			<view class="regist">
				<tui-button @click="regist" type="danger" shape="circle">注册</tui-button>
			</view>
		</view>
		<view></view>
	</view>
</template>

<script>
	import {
		// sendDm,
		// sendMsm,
		// register,
		isRegist,
	} from '@/api/regist.js';
	import {
		tokenUtil,
	} from '@/utils/token.js';
	import {
		isMobile,
		isEmail,
	} from '@/utils/validate.js';
	import {
		sendValidCode,
		register,
	} from '@/api/v1/regist.js';

	export default {
		data() {
			return {
				T: true,
				count: 0,
				userInfo: {
					userBasic: {},
					validCode: null,
					pwd:null,
				},
				value: '',
				code: '',
				pwd: '',
				pwdRe: ''

			};
		},
		methods: {
			//发送验证码
			async getCode() {

				let isE = isEmail(this.value);
				let isSend = false;
				if (!isE) {
					uni.showToast({
						title: '请输入正确手机号或邮箱',
					});
					return;
				}
				//验证当前邮箱是否被注册
				this.userInfo.email = this.value;
				let data = {};
				data.userEmail = this.value;
				await sendValidCode(data).then(
					res => {
						if (!res.success) {
							uni.showToast({

								title: '该邮箱被注册',
							});
						} else {
							uni.showToast({
								title: '发送成功',
							});
							isSend = true;
						}
					},
				);
				if (isSend) {
					this.T = false;
					this.count = 60;
					var times = setInterval(() => {
						this.count--; //递减
						if (this.count <= 1) {
							this.T = true;
							clearInterval(times);
						}
					}, 1000);
				}
			},
			regist() {
				if(this.pwd!=this.pwdRe){
					uni.showToast({
						title: '密码不一致！',
						icon:'error'
					});
				}
				let isE = isEmail(this.value);
				//去除所有空格
				this.userInfo.validCode = this.code.replace(/\s+/g, '');
				if (isE) {
					this.userInfo.userBasic.userEmail = this.value;
					this.userInfo.userBasic.pwd = this.pwd;
				} else {
					uni.showToast({
						title: '请输入正确手机号或邮箱',
						icon:'none'
					});
					return;
				}

				register(this.userInfo).then(res => {
					console.log(res);
					if (res.success) {
						uni.showToast({
							title: '注册成功!',
						});
						const user = res.data.userBasic;
						tokenUtil.set(res.data.authHeader);
						uni.setStorageSync('userBasic', user);

						setTimeout(() => {
							uni.reLaunch({
								url: '/pages/index/index',
							});
						}, 500);
					} else {
						uni.showToast({
							title: res.msg,
						});
					}
				});
			},
		},
	};
</script>

<style scoped>
	@import url(./regist.css);
</style>