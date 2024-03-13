<template>
	<view class="content" :style="'margin-top:'+vHeight+'rpx'">
		<tui-navigation-bar backgroundColor="#fff" :isFixed="true" :isOpacity="false" v-if='top_show'>
			<view class="fixed-top">
				<view class="fixed-user">
					<image :src="avatarUrl" class="avatar" mode="aspectFill"
						@click="previewImgae(userDetail.uAvatar)" />
					<view class="username">{{ userDetail.username }}</view> <!-- TODO 加入sex-->
				</view>
			</view>
		</tui-navigation-bar>

		<tui-navigation-bar backgroundColor="#fff" :isFixed="false" :isOpacity="false" v-else>
			<view class="top">
				<view class="right">
					<view class="item">
						<tui-icon name="manage" class="icon-item-manage" color="#fff" size="30"></tui-icon>
					</view>
					<view class="item">
						<navigator url="/pages/setting/setting">
							<tui-icon name="setup" class="icon-item-setup" color="#fff" size="30"></tui-icon>
						</navigator>
					</view>
				</view>
			</view>
		</tui-navigation-bar>


		<!-- 头 -->

		<!-- 显示图片 -->
		<view class="image">
			<image :src="userDetail.cover" v-if="userDetail" mode="aspectFill"
				@click="previewImgae(userDetail.cover)" />
			<image src="/static/images/toast/img_nodata.png" v-else mode="aspectFill" />
		</view>

		<!-- 主体 -->
		<view class="main">
			<view class="top">
				<view class="user">
					<view class="user-left">
						<image :src="avatarUrl" class="avatar" mode="aspectFill" @click="previewImgae(avatarUrl)" />
						<view class="user-content">
							<h3>{{ userDetail.nickname }}</h3>
							<view class="user-id f">ID: {{ userDetail.userName }}</view>
							<view class="descrpition f">简介: {{ userDetail.uAbout }}</view>
						</view>
					</view>
					<view class="user-right">
						<tui-button type="danger" shape="circle" @click="editUserDetail" height="60rpx" width="140rpx"
							:size="28">编辑
						</tui-button>
					</view>
				</view>
				<view class="info">
					<p @click="getAllFriend(1)">关注 {{ userDetail.followCount }}</p>
					<p @click="getAllFriend(0)">粉丝 {{ userDetail.fansCount }}</p>
					<p>动态 {{ userDetail.blogCount }}</p>
				</view>
				<view class="">
					<tui-tabs :tabs="tabs" :currentTab="currentTab" @change="change" sliderBgColor="red"
						selectedColor="red" itemWidth="50%"></tui-tabs>
				</view>
			</view>

			<view class="zhuti">
				<view v-if="userDetail">
					<Trend v-if="currentTab == 0" :uid="userDetail.uuid" @cancelUp='cancelUp' :seed='seed'></Trend>
					<Album v-if="currentTab == 1" :seed='seed' :uid='uid'></Album>
					<Collection v-if="currentTab == 2" :uid='uid' :seed='seed'></Collection>
				</view>
				<view class="nologin" v-else>
					<button @click="login">请先登录</button>
				</view>
			</view>
		</view>

		<tui-modal :show="show" @click="confirm" @cancel="hide" content="取消上传" :button="radio" width="50%"
			padding="15rpx 40rpx" :fadeIn='true'></tui-modal>

	</view>
</template>

<script>
	import Trend from '@/pages/user/trends/trends';
	import Album from '@/pages/user/albums/albums';
	import Collection from '@/pages/user/collections/collections';

	import {
		deleteImgs,
	} from '@/api/imgDetail.js';
	import {
		getUserInfo,
	} from '@/api/v1/user.js';

	export default {
		components: {
			Trend,
			Album,
			Collection,
		},
		data() {
			return {
				current: 0,
				currentTab: 0,
				tabs: [{
					name: '动态',
				}, {
					name: '专辑',
				}, {
					name: '收藏',
				}],
				userDetail: {},
				avatarUrl: null,
				coverUrl: null,
				seed: 0,
				uid: '',
				show: false,

				radio: [{
						text: '取消',
						type: 'white',
					},
					{
						text: '确定',
						type: 'red',
					},
				],

				mid: '',

				//页面初始化高度
				screenHeight: 0,

				top_show: false,

				vHeight: 0,
			};
		},
		onLoad(options) {
			if (typeof options.currentTab != 'undefined' || options.currentTab != null) {
				this.currentTab = options.currentTab;
			}
			this.screenHeight = uni.getSystemInfoSync().screenHeight;
		},

		onReachBottom() {
			this.seed = Math.random();
		},
		onShow() {
			this.getUser();
			this.uid = uni.getStorageSync('userDetail').uuid;
			this.seed = Math.random();

		},
		onPageScroll(e) {

			const that = this;
			if (e.scrollTop >= 20) {
				this.top_show = true;
				uni.createSelectorQuery().select('.content').boundingClientRect(function(data) {
					that.vHeight = 220;
				}).exec();
			} else {
				this.top_show = false;
				uni.createSelectorQuery().select('.content').boundingClientRect(function(data) {
					that.vHeight = 0;
				}).exec();
			}

		},
		methods: {
			change(e) {
				this.currentTab = e.index;
			},
			setting() {
				uni.navigateTo({
					url: '/pages/setting/setting',
				});
			},


			cancelUp(mid) {
				this.show = true;
				this.mid = mid;
			},

			confirm(e) {
				let index = e.index;
				if (e.index == 0) {
					this.show = false;
				} else {
					let arr = [];
					arr.push(this.mid);
					deleteImgs(arr, this.userDetail.id).then(res => {
						uni.showToast({
							title: '取消成功',
						});
						this.show = false;
					});
				}
			},
			hide() {
				this.show = false;
			},

			editUserDetail() {
				uni.navigateTo({
					url: '/pages/user/info/info?uid= ' + this.userDetail.uuid
				});
			},
			login() {
				uni.navigateTo({
					url: '/pages/login/login',
				});
			},
			getUser() {

				let params = uni.getStorageSync('userBasic').uuid;

				getUserInfo(params).then(res => {
					if (res.success) {
						this.userDetail = res.data;
						// console.log(res.data);
						this.avatarUrl = res.data.uAvatar;
						uni.setStorage({
							key: 'aVatarUrl',
							data: this.avatarUrl,
						});
						uni.setStorage({
							key: 'coverUrl',
							data: res.data.cover,
						});
						uni.setStorage({
							key: 'TrendsUuid',
							data: res.data.uuid
						});
					} else {
						uni.showTost({
							title: '网络错误',
							icon: 'error',
						}, );
					}

				});

			},

			getAllFriend(type) {
				uni.navigateTo({
					url: '/pages/user/allUser/allUser?type=' + type + '&uid=' + this.userDetail.id,
				});
			},

			//-----------------------------------预览图片----------------
			previewImgae(url) {

				let that = this;
				let path = [];
				path.push(url);

				uni.previewImage({
					current: 0, // 当前显示图片的索引值
					urls: path, // 需要预览的图片列表，photoList要求必须是数组
					longPressActions: {
						itemList: ['保存'],
						success: function(data) {

							//进行保存
							if (data.tapIndex == 0) {
								let p = that.downLoadImg(url);
								p.then((data) => {
									uni.showToast({
										title: data,
										icon: 'none',
									});
								});
							}
						},
						fail: function(err) {
							return;
						},
					},
				});
			},

			// 保存图片至本地
			downLoadImg(path) {
				//下载图片资源至本地，返回文件的本地临时路径
				return new Promise((resolve, reject) => {
					uni.downloadFile({
						url: path,
						success: (res) => {
							if (res.statusCode === 200) {
								//保存图片至相册
								uni.saveImageToPhotosAlbum({
									filePath: res.tempFilePath,
									success: function() {
										resolve('保存成功');
									},
									fail: function() {
										reject('保存失败');
									},
								});
							}
						},
					});
				});
			},
		},
	};
</script>

<style scoped>
	@import url(./user.css);
</style>