<template>
	<view class="content" :style="'margin-top:'+vHeight+'rpx'">
		<tui-navigation-bar backgroundColor="#fff" :isFixed="true" :isOpacity="false" v-if='top_show'>
			<view class="fixed-top">
				<view class="fixed-user">
					<image :src="userInfo.uAvatar" class="avatar" mode="aspectFill"
						@click="previewImgae(userInfo.uAvatar)" />
					<view class="username">{{ userInfo.username }}</view>
				</view>
			</view>
		</tui-navigation-bar>

		<tui-navigation-bar backgroundColor="#fff" :isFixed="false" :isOpacity="false">
			<view class="top">
				<view @click="back">
					<tui-icon name="arrowleft" color="#fff" size="30"></tui-icon>
				</view>
			</view>
		</tui-navigation-bar>
		<!-- 显示图片 -->
		<view class="image">
			<image :src="userInfo.cover" v-if="userInfo" mode="aspectFill" @click="previewImgae(userInfo.cover)" />
			<image src="/static/images/toast/img_nodata.png" v-else mode="aspectFill" />
		</view>

		<!-- 主体 -->
		<view class="main">
			<view class="top">
				<view class="user">
					<view class="user-left">
						<image :src="userInfo.uAvatar" class="avatar" mode="aspectFill"
							@click="previewImgae(userInfo.uAvatar)" />
						<view class="user-content">
							<h3>{{ userInfo.nickname }}</h3>
							<view class="user-id f">id:{{ userInfo.userName }}</view>
							<view class="descrpition f">{{ userInfo.uAbout}} </view>
						</view>

					</view>
					<view class="user-right">
						<view @click="chat(userInfo.id)" class="user-message">
							<tui-icon name="message" color="#797979"></tui-icon>
						</view>
						<tui-button v-if="isFollow==true" @click="follow(userInfo.uuid)" type="gray" height="66rpx"
							size=24 width="120rpx">已关注</tui-button>
						<tui-button v-else @click="follow(userInfo.uuid)" type="danger" height="66rpx"
							width="120rpx">关注</tui-button>
					</view>
				</view>
				<view class="info">
					<p @click="getAllFriend(1)">关注 {{ userInfo.followCount }}</p>
					<p @click="getAllFriend(0)">粉丝 {{ userInfo.fansCount }}</p>
					<p>动态 {{ userInfo.trendCount }}</p>
				</view>
				<view class="">
					<tui-tabs :tabs="tabs" :currentTab="currentTab" @change="change" sliderBgColor="red"
						selectedColor="red" itemWidth="50%"></tui-tabs>
				</view>
			</view>

			<view class="zhuti">
				<view v-if="userInfo">
					<Trend v-if="currentTab == 0" :uid='userInfo.uuid' :seed='seed'> </Trend>
					<Album v-if="currentTab == 1" :uid='uid' :seed='seed'></Album>
					<Collection v-if="currentTab == 2" :uid='uid' :seed='seed'></Collection>
				</view>
				<view class="nologin" v-else>
					<button @click="login">请先登录</button>
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	import Trend from "@/pages/user/trends/trends"
	import Album from "@/pages/user/albums/albums"
	import Collection from "@/pages/user/collections/collections"
	import {
		getUserInfo,
		changeFollowStatus,
		isFollow
	} from "@/api/v1/user.js"



	export default {
		components: {
			Trend,
			Album,
			Collection
		},
		data() {
			return {
				current: 0,
				currentTab: 0,
				tabs: [{
					name: "动态"
				}, {
					name: "专辑"
				}, {
					name: "收藏"
				}],
				userInfo: {},
				seed: 0,
				uuid: '', //别人的id
				isFollow: false,
				vHeight: 0,
				//页面初始化高度
				screenHeight: 0,
				top_show: false,

				currentUser: null //登录中的用户
			}
		},

		onLoad(option) {
			this.uuid = option.uid
			uni.setStorage({
				key: 'TrendsUuid',
				data: this.uuid
			})
			this.isFollow = option.isFollow
			if (typeof option.currentTab != 'undefined' || option.currentTab != null) {
				this.currentTab = option.currentTab
			}
			this.screenHeight = uni.getSystemInfoSync().screenHeight;

			this.currentUser = uni.getStorageSync('userBasic');
			getApp().globalData.currentUser = this.currentUser;
			this.getUserInfo(this.uuid)
			this.getFollowStats(this.uuid);

		},

		onShow() {


			// this.isFollow()
			this.seed = Math.random()
		},

		onPageScroll(e) {

			const that = this
			if (e.scrollTop >= 20) {
				this.top_show = true
				uni.createSelectorQuery().select('.content').boundingClientRect(function(data) {
					that.vHeight = 220
				}).exec()
			} else {
				this.top_show = false
				uni.createSelectorQuery().select('.content').boundingClientRect(function(data) {
					that.vHeight = 0
				}).exec()
			}

		},

		methods: {



			back() {
				uni.setStorage({
					key: 'TrendsUuid',
					data: uni.getStorageSync('userBasic').uuid
				});
				this.isFollow = undefined
				uni.navigateTo({
					url: '/pages/index/search/search'
				})
			},
			change(e) {
				this.currentTab = e.index
			},
			login() {
				uni.reLaunch({
					url: "/pages/login/login"
				})
			},
			getFollowStats(touid) {
				isFollow(this.currentUser.uuid, touid).then(res => {
					this.isFollow = res.data;
				});
			},
			getUserInfo(uid) {
				getUserInfo(uid).then(res => {
					this.userInfo = res.data;
				})
			},
			chat(uid) {
				console.log(uid)
				uni.navigateTo({
					url: "/pages/message/privateChat/privateChat?to=" + uid
				})
			},



			//关注用户
			follow(toId) {
				const uuid = this.currentUser.uuid;
				changeFollowStatus(uuid, toId).then(res => {
					if (res.success) {
						this.isFollow = res.data;
						console.log("change" + this.isFollow)
					} else {
						uni.showToast({
							icon: "error",
							title: res.msg
						})
					}
				})
			},


			getAllFriend(type) {
				uni.navigateTo({
					url: "/pages/user/allUser/allUser?type=" + type + "&uid=" + this.uid
				})
			},
			onReachBottom() {
				this.seed = Math.random()
			},

			previewImgae(url) {

				let that = this
				let path = []
				path.push(url)

				uni.previewImage({
					current: 0, // 当前显示图片的索引值
					urls: path, // 需要预览的图片列表，photoList要求必须是数组
					longPressActions: {
						itemList: ['保存'],
						success: function(data) {

							//进行保存
							if (data.tapIndex == 0) {
								let p = that.downLoadImg(url)
								p.then((data) => {
									uni.showToast({
										title: data,
										icon: "none"
									});
								})
							}
						},
						fail: function(err) {
							return
						}
					}
				})
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
										resolve("保存成功")
									},
									fail: function() {
										reject("保存失败")
									}
								});
							}
						}
					})
				})
			},
		}
	}
</script>

<style scoped>
	@import url(./otherUser.css);
</style>