<template>
	<view class="container">
		<scroll-view scroll-y class="page" @scrolltolower="loadData" refresher-enabled="true"
			:refresher-triggered="triggered" @refresherrefresh="onRefresh" :scroll-top="scrollTop" @scroll="scroll">
			<ul v-if="firstLoading">
				<li v-for="index in 4">
					<view class="item">
						<view class="top">
							<view class="avatar-user">
								<view class="avatar">
									<image src="/static/images/basic/default.png" mode="aspectFill" :lazy-load='true' />
								</view>
								<view class="user">
									<view class="name" style="height: 20rpx;background-color: #F2F2F2;"></view>
									<view class="time" style="height: 20rpx;background-color: #F2F2F2;"></view>
								</view>
							</view>
						</view>
						<view class="content" style="height: 40rpx;background-color: #F2F2F2;">
						</view>
						<view class="main">
							<view class="img-list">
								<view v-for="index in 3">
									<image src="/static/images/basic/default.png" mode="aspectFill" :lazy-load='true'
										class="fadeImg" />
								</view>
							</view>
						</view>
						<view class="fotter" style="background-color: #F2F2F2;">
						</view>
						<!-- 底部聊天 -->
					</view>
				</li>
			</ul>
			<ul v-else>
				<li v-for="(item, index) in dataList" :key="index">
					<view class="item">
						<view class="top">
							<view class="avatar-user">
								<view class="avatar" @click="getUserInfo(item.userId)">
									<image :src="item.avatar" mode="aspectFill" :lazy-load='true' />
								</view>
								<view class="user">
									<view class="name">{{ item.username }}</view>
									<view class="time">{{ item.time }}</view>
								</view>
							</view>
						</view>
						<view class="content">
							{{ item.content }}
						</view>
						<view class="main">

							<view class="img-list" @click="toMain(item.mid)">
								<view v-for="(img, index) in item.imgsUrl" :key="index">

									<image :src="img" mode="aspectFill" :lazy-load='true' class="fadeImg" />
									<!-- <ImgFade :src="img" ></ImgFade> -->
								</view>

							</view>

							<view class="collection-album" @click="toAlbum(item.albumId)">
								<view class="left">
									<tui-icon name="upload" size="18" color="#fd6d49"></tui-icon>
									<view class="content1">更新专辑:</view>
									<view class="content2">{{ item.albumName }}</view>
								</view>
								<view class="right">
									<tui-icon name="arrowright" size="24"></tui-icon>
								</view>
							</view>

						</view>


						<view class="fotter">
							<view class="icon">
								<tui-icon name="agree-fill" size="16" v-if="item.isAgree"
									@click="cancelAgreeImg(item, index)"></tui-icon>
								<tui-icon name="agree" size="16" v-else @click="agreeImg(item, index)"></tui-icon>
								<view class="count">{{ item.agreeCount }}</view>
							</view>
							<view class="icon" @click="getComment(item.mid)">
								<tui-icon name="message" size="16"></tui-icon>
								<view class="count">{{ item.commentCount }}</view>
							</view>
							<view class="icon">
								<tui-icon name="more" size="16"></tui-icon>
							</view>
						</view>
						<!-- 底部聊天 -->
					</view>
				</li>

				<!-- </uni-list> -->
			</ul>

			<view class="loadStyle" v-if="!isEnd && loading">
				<tui-icon name="loading" :size="18"></tui-icon>
			</view>
			<view class="loadStyle" v-if="isEnd">我也是有底线的~</view>

		</scroll-view>

		<trend-comment :popupShow="popupShow" @popup="popup" :mid="mid" :seed="seed"></trend-comment>

	</view>
</template>

<script>
	import {
		getAllFollowTrends
	} from "@/api/interest.js"
	import {
		addBrowseRecord
	} from "@/api/browseRecord.js"
	import TrendComment from "@/components/trendComment.vue"
	import {
		agree,
		cancelAgree
	} from "@/api/agreeCollect.js"
	import {
		timeAgo
	} from "@/utils/webUtils.js"
	import {
		loadImageEnd,loadImageEnd2
	} from "@/utils/utils.js"
	export default {
		components: {
			TrendComment,
		},
		data() {
			return {
				userInfo: {},
				triggered: false,
				page: 1,
				limit: 4,
				userInfo: {},
				dataList: [],
				popupShow: false,
				isEnd: false, //是否到底底部了
				loading: false, //是否正在加载
				total: 0,
				mid: '',
				seed: 0,
				scrollTop: 0,
				old: {
					scrollTop: 0
				},

				firstLoading: false

			}
		},
		created() {
			this.userInfo = uni.getStorageSync("userInfo")
			if (typeof this.userInfo == 'undefined' || this.userInfo == null || this.userInfo == '') {
				uni.showToast({
					title: "用户未登录",
					icon: 'none',
				})
				return;
			} else {
				this.getAllFollowTrends()
			}

		},

		methods: {
			getComment(mid) {

				this.mid = mid
				this.seed = Math.random()
				this.popupShow = true;
				uni.hideTabBar()
			},
			popup(popupShow) {

				this.popupShow = popupShow
			},

			getData(page, limit) {
				
				let params = {
					uid: this.userInfo.id
				}

				new Promise((resolve, rej) => {
					let dataObj = {
						"imgList": [],
						"list": []
					}

					getAllFollowTrends(page, limit, params).then(res => {
						console.log(res)
						res.data.forEach(e => {
							e.time = timeAgo(e.time)
							e.imgsUrl = JSON.parse(e.imgsUrl)
							dataObj.list.push(e)
							dataObj.imgList.push(...e.imgsUrl)
							dataObj.imgList.push(e.avatar)
						})

						this.total = res.data.length

						resolve(dataObj)
					})

				}).then(data => {
					// 在手机上无法使用
					// loadImageEnd(data.imgList, () => {
					// 	this.triggered = false
					// 	this.firstLoading = false
					// 	this.dataList.push(...data.list)
					// 	console.log(this.dataList)
					// })
					
					//
	                    this.triggered = false
						this.firstLoading = false
						this.dataList.push(...data.list)
				})
			},

			getAllFollowTrends() {
				this.getData(this.page, this.limit)
			},

			onRefresh() {
				this.triggered = true;

				this.page = 1

				this.dataList = []

				this.getData(this.page, this.limit)
			},


			loadData() {

				this.loading = true
				if (this.total < this.limit) {
					this.isEnd = true
					return
				}
				this.page = this.page + 1;

				this.getData(this.page, this.limit)

			},

			getUserInfo(uid) {
				uni.navigateTo({
					url: "/pages/otherUser/otherUser?uid=" + uid
				})
			},

			toMain(mid) {

				let data = {}
				data.uid = this.userInfo.id
				data.mid = mid

				addBrowseRecord(data).then(res => {
					uni.navigateTo({
						url: "/pages/main/main?mid=" + mid
					})
				})
			},
			toAlbum(aid) {
				uni.navigateTo({
					url: "/pages/user/albums/albumInfo?albumId=" + aid
				})
			},

			agreeImg(item, index) {
				let data = {}
				data.uid = uni.getStorageSync("userInfo").id
				data.type = 1
				data.agreeCollectId = item.mid
				data.agreeCollectUid = item.userId

				agree(data).then(res => {
					this.dataList[index].agreeCount = this.dataList[index].agreeCount * 1 + 1
					this.dataList[index].isAgree = true
				})
			},

			cancelAgreeImg(item, index) {

				let data = {}
				data.uid = uni.getStorageSync("userInfo").id
				data.agreeCollectId = item.mid
				data.agreeCollectUid = item.userId
				data.type = 1
				cancelAgree(data).then(res => {
					this.dataList[index].agreeCount = this.dataList[index].agreeCount * 1 - 1
					this.dataList[index].isAgree = false
				})
			},
			scroll(e) {

				this.old.scrollTop = e.detail.scrollTop
			},

		}
	}
</script>

<style scoped>
	@import url(./interest.css);
</style>