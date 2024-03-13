<template>
	<view class="container">
		<ul>
			<uni-list :data='dataList' :total='total'>
				<li v-for="(item, index) in dataList" :key="index">
					<view class="item">
						<view class="top">
							<view class="avatar-user">
								<view class="avatar">
									<image :src="userInfo.uAvatar" mode="aspectFill" :lazy-load='true' />
								</view>
								<view class="user">
									<view class="name">{{ userInfo.nickname }}</view>
									<view class="time"> 发布时间：{{timeAgo(item.publishTime)}}</view>
								</view>
							</view>
						</view>
						<view class="content">
							{{ item.title }}
						</view>
						<view class="main">

							<view class="img-list" @click="toMain(item.id,item.status)">
								<view v-for="(img, index) in item.imageList" :key="index">
									<image :src="img" mode="aspectFill" :lazy-load='true' class="fadeImg" />
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
						<view class="fotter"></view>
					</view>
				</li>
			</uni-list>
		</ul>

		<view class="loadStyle" v-if="!isEnd && loading">正在加载中</view>
		<view class="loadStyle" v-if="isEnd">我也是有底线的~</view>
	</view>
</template>

<script>
	import {
		getBlogPageByUser,
		getUserInfo
	} from "@/api/v1/user.js"
	import {
		addBrowseRecord
	} from "@/api/browseRecord.js"
	import {
		timeAgo
	} from "@/utils/webUtils.js"
	import {
		publish,
		updateImgDetail,
		updateStatus,
		deleteImgs
	} from '@/api/imgDetail.js'
	import {
		appConfig
	} from '@/config/config'
	export default {
		props: {
			uid: String,
			seed: Number,
		},
		data() {
			return {
				page: 1,
				limit: 5,
				userInfo: {},
				dataList: [],
				isEnd: false, //是否到底底部了
				loading: false, //是否正在加载
				total: 0,
				type: 0,
				avatarUrl: '',

			}
		},

		watch: {
			seed(newVal, oldVal) {
				this.userInfo.uuid = uni.getStorageSync("TrendsUuid");
				this.loadData(this.userInfo.uuid)
			}
		},

		created() {
			this.userInfo.uuid = uni.getStorageSync("TrendsUuid");
			this.getUserInfo(this.userInfo.uuid)
		},

		onShow() {
			this.userInfo.uuid = uni.getStorageSync("TrendsUuid");
			this.page = -1;
			this.dataList = [];
			this.loadData(this.userInfo.uuid);
		},

		onUnload() {
			this.page = 0;
			this.dataList = [];
		},

		methods: {

			getTrendByUser() {

				getBlogPageByUser(this.page, this.limit, this.userInfo.uuid).then(res => {
					res.data.records.forEach(Element => {
						Element.publishTime = new Date(Element.publishTime);
						// Element.publishTime = timeAgo(Element.publishTime);
						this.dataList.push(Element);
					});
					this.total = res.data.total;
				});
			},
			timeAgo(date) {
				return timeAgo(date) == "" ? "刚刚" : timeAgo(date);
			},
			getUserInfo(uid) {
				getUserInfo(uid).then(res => {
					if (res.success) {
						this.userInfo = res.data;
						this.avatarUrl = res.data.uAvatar;
						if (res.data.uuid == uni.getStorageSync("userBasic").uuid) {
							this.type = 1 //是自己
						}
					}
				});

				this.getTrendByUser();


			},

			loadData() {

				this.loading = true
				setTimeout(() => {
					if (this.dataList.length >= this.total) {
						this.isEnd = true
						return
					}
					this.page = this.page + 1;
					let params = {
						pubUuid: this.userInfo.uuid,
					}

					getBlogPageByUser(this.page, this.limit, this.userInfo.uuid).then(res => {
						res.data.records.forEach(Element => {
							Element.publishTime = new Date(Element.publishTime);
							// Element.publishTime = timeAgo(Element.publishTime);
							this.dataList.push(Element);
						});
						this.total = res.data.total;
					});
				}, 200);
				this.loading = false
			},

			cancelUpload(mid) {
				this.$emit('cancelUp', mid)
			},

			toMain(blogId, status) {

				uni.navigateTo({
					url: '/pages/main/note?blogId=' + blogId
				})

				// let data = {}
				// data.uid = this.userInfo.id
				// data.mid = mid

				// addBrowseRecord(data).then(res => {
				// 	uni.navigateTo({
				// 		url: "/pages/main/main?mid=" + mid
				// 	})
				// })
			},

			toAlbum(aid) {
				uni.navigateTo({
					url: "/pages/user/albums/albumInfo?albumId=" + aid
				})
			}
		}
	}
</script>

<style scoped>
	@import url(./trends.css);
</style>