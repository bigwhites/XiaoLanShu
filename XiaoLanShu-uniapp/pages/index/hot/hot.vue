<template>
	<view class="container">
		
		
		
		<scroll-view  scroll-y class="page" @scrolltolower="loadData" >
			
			<view v-if="firstLoading" class="item-hot" v-for="index in 5" >
				<view class="item-hot-left"></view>
				<view class="item-hot-right">
					<view class="item-hot-top">
						<view class="top-left">
							<view class="avatar" >
								<image src="/static/images/basic/default.png" mode="aspectFill" />
							</view>
						</view>
					</view>
					<view class="images" @click="toMain(item.id)">
						<view v-for="index in 3 " >
							<image src="/static/images/basic/default.png" mode="aspectFill"   />
						</view>
			
					</view>
			
				</view>
			</view>
			   
			
				<view v-else class="item-hot" v-for="(item, index) in dataList" :key="index">
					<view class="item-hot-left">{{ index + 1 }}</view>
					<view class="item-hot-right">
						<view class="item-hot-top">
							<view class="top-left">
								<view class="avatar" @click="getUserInfo(item.userId)">
									<image :src="item.avatar" mode="aspectFill" :lazy-load='true' />
								</view>
								<view class="username">
									{{ item.username }}
								</view>
							</view>
							<view class="top-right">
								{{ item.agreeCount }} <tui-icon name="like-fill" size="14" color="#ff5500"></tui-icon>
							</view>
						</view>

						<view class="content">
							{{ item.content }}
						</view>

						<view class="images" @click="toMain(item.id)">

							<view v-for="(img, index) in item.imgsUrl " v-show="index < 3">

								<image :src="img" mode="aspectFill" :lazy-load='true' class="fadeImg" />
							</view>

							<view class="nums" v-if="item.count > 3">+{{ item.count - 3 }}</view>
						</view>

					</view>
				</view>
			<view class="loadStyle" v-if="!isEnd && loading">
				<tui-icon name="loading" :size="18"></tui-icon>
			</view>
			<view class="loadStyle" v-if="isEnd">我也是有底线的~</view>
		</scroll-view>
	</view>
</template>

<script>
	import {
		getHot
	} from '@/api/imgDetail.js'
	import {
		addBrowseRecord
	} from "@/api/browseRecord.js"
	import {
		loadImageEnd
	}from "@/utils/utils.js"
	export default {

		data() {
			return {
				page: 1,
				limit: 5,
				total: 0,
				dataList: [],
				loading: false,
				isEnd: false,
				firstLoading: true
			}
		},
		created() {
			this.getHot()
		},
		methods: {
			
			getData(page,limit){
				new Promise((resolve,rej)=>{
					
					let dataObj = {
						 "imgList":[],
						 "list":[]
					}
					
					getHot(page,limit).then(res => {
						dataObj.list = res.data.records
						res.data.records.forEach(item=>{
							dataObj.list.push(item)
							dataObj.imgList.push(item.imgsUrl)
						})
						this.total = res.data.total
						resolve(dataObj)
					})
					
				}).then(data=>{
					//loadImageEnd(data.imgList,()=>{		 
					    this.dataList.push(...data.list)
						this.firstLoading = false
					//})
				})
			},
			
			getHot() {
				this.getData(this.page,this.limit)
			},
			
			loadData() {
				this.loading = true
				if (this.dataList.length >= this.total) {
					this.isEnd = true
					return
				}
				this.page = this.page + 1;
				this.getData(this.page,this.limit)
			},
			
			getUserInfo(uid) {
				if (uid == uni.getStorageSync("userInfo").id) {
					uni.switchTab({
						url: "/pages/user/user"
					})
				} else {
					uni.navigateTo({
						url: "/pages/otherUser/otherUser?uid=" + uid
					})
				}
			},
			toMain(mid) {
				let data = {}
				data.uid = uni.getStorageSync("userInfo").id
				data.mid = mid

				addBrowseRecord(data).then(res => {
					uni.navigateTo({
						url: "/pages/main/main?mid=" + mid
					})
				})
			},
		}
	}
</script>

<style scoped>
	@import url(./hot.css);
</style>