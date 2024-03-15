<template>
	<view class="container">
		<!-- <view class="main"> -->
		<uv-waterfall ref="waterfall" v-model="list" :add-time="10" :left-gap="leftGap" :right-gap="rightGap"
			:column-gap="columnGap" @changeList="changeList">
			<!-- 第一列数据 -->
			<template v-slot:list1>
				<!-- 为了磨平部分平台的BUG，必须套一层view -->
				<view>
					<view v-for="(item, index) in list1" :key="item.id" class="waterfall-item">
						<view class="waterfall-item__image" :style="[imageStyle(item)]" @click="toNote(item.blogId)">
							<image :src="item.coverFileName" mode="widthFix" :style="{width:item.width+'px'}">
							</image>
						</view>
						<view class="waterfall-item__ft" @click="toNote(item.blogId)">
							<view class="waterfall-item__ft__title">
								<text class="value">{{item.title}}</text>
							</view>
							<view class="waterfall-item__ft__desc uv-line-2">
								<image :src="item.pubUAvatar" mode="aspectFill"
									style="width: 30px;height: 30px;
											background: #fff;-webkit-animation: fadeinout 2s linear forwards;animation: fadeinout 2s linear forwards;" />
								<text class="value">{{item.pubUNickname+' '}}</text>
							</view>
							<view style="display: flex;">
								<uv-icon style="margin-right: 17px;" name="eye" color="#6d6d6d" size="20"
									:label="item.viewCount"></uv-icon>
								<!-- <uv-icon name="heart" size="20" :label="item.agreeCount"></uv-icon> -->
								<!-- <uv-icon v-else name="heart" size="20" :label="item.agreeCount"></uv-icon> -->
							</view>
						</view>
					</view>
				</view>
			</template>
			<!-- 第二列数据 -->
			<template v-slot:list2>
				<!-- 为了磨平部分平台的BUG，必须套一层view -->
				<view>
					<view v-for="(item, index) in list2" :key="item.id" class="waterfall-item">
						<view class="waterfall-item__image" :style="[imageStyle(item)]" @click="toNote(item.blogId)">
							<image :src="item.coverFileName" mode="widthFix" :style="{width:item.width+'px'}">
							</image>
						</view>
						<view class="waterfall-item__ft" @click="toNote(item.blogId)">
							<view class="waterfall-item__ft__title">
								<text class="value">{{item.title}}</text>
							</view>
							<view class="waterfall-item__ft__desc uv-line-2">
								<image :src="item.pubUAvatar" mode="aspectFill" style="width: 30px;height: 30px;
			background: #fff;-webkit-animation: fadeinout 2s linear forwards;animation: fadeinout 2s linear forwards;" />
								<text class="value">{{item.pubUNickname+' '}}</text>

							</view>
							<view style="display: flex;">
								<uv-icon style="margin-right: 17px;" name="eye" color="#6d6d6d" size="20"
									:label="item.viewCount"></uv-icon>
								<!-- <uv-icon name="heart" size="20" :label="item.agreeCount"></uv-icon> -->
								<!-- <uv-icon v-else name="heart" size="20" :label="item.agreeCount"></uv-icon> -->
							</view>
						</view>

					</view>
				</view>
			</template>
		</uv-waterfall>
		<!-- </view> -->
	</view>
</template>

<script>
	import {
		collectionHistory
	} from "@/api/v1/note.js"

	export default {
		components: {},
		props: {
			uid: String,
			seed: Number,
		},
		data() {
			return {
				page: 1,
				pageSize: 5,
				total: 0,
				list: [],
				list1: [],
				list2: [],
				isEnd: false,
				leftGap: 10,
				rightGap: 10,
				columnGap: 10,
			}
		},
		computed: {
			imageStyle(item) {
				return item => {
					const v = uni.upx2px(750) - this.leftGap - this.rightGap - this.columnGap;
					const w = v / 2;
					const rate = w / item.w;
					const h = rate * item.h;
					return {
						width: w + 'px',
						height: h + 'px'
					}
				}
			}
		},
		created() {
			console.log('crea')
			this.getData();
		},
		onShow() {
			console.log('sshpow')
			this.list = [];
			this.list1 = [];
			this.list2 = [];
			this.isEnd = false;
			this.total = 0;
			this.page = 1;
			this.getData();
		},
		onReachBottom() {
			this.getData();
		},
		methods: {
			change(e) {
				this.currentTab = e.index
			},
			changeList(e) {
				this[e.name].push(e.value);
			},
			getData() {
				if (this.isEnd) {
					return;
				}
				console.log('ssss')
				this.isEnd = false;
				collectionHistory(
					uni.getStorageSync('userBasic').uuid,
					this.page,
					this.pageSize
				).then(res => {
					if (res.success) {
						this.page = this.page + 1;
						if (this.page > res.data.pages) {
							this.isEnd = false;
						}
						this.list.push(...res.data.records);
					}
				});
			},
			toNote(blogId) {
				uni.navigateTo({
					url: '/pages/main/note?blogId=' + blogId
				});
			},
		}
	}
</script>

<style scoped>
	@import url(./collections.css);
</style>
<style>
	page {
		background: #f1f1f1;
	}
</style>
<style scoped lang="scss">
	$show-lines: 1;
	@import '@/uni_modules/uv-ui-tools/libs/css/variable.scss';
	// @import url(./history.css);

	.waterfall-item {
		overflow: hidden;
		margin-top: 10px;
		border-radius: 6px;
	}

	.waterfall-item__ft {
		padding: 20rpx;
		background: #fff;

		&__title {
			margin-bottom: 10rpx;
			line-height: 48rpx;
			font-weight: 700;

			.value {
				font-size: 32rpx;
				color: #303133;
			}
		}

		&__desc .value {
			font-size: 28rpx;
			color: #606266;
		}

		&__btn {
			padding: 10px 0;
		}
	}
</style>