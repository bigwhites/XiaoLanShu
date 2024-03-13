<template>
	<view class="container">
		<tui-navigation-bar backgroundColor="#fff" :isFixed="true" :isOpacity="false">
			<tui-tabs :tabs="tabs" :currentTab="currentTab" itemWidth="50%" @change="change" sliderBgColor="#ff0000"
				selectedColor="#ff0000"></tui-tabs>
		</tui-navigation-bar>

		<view @touchstart="start" @touchend="end">
			<interest v-if="currentTab == 0"></interest>
			<hot v-if="currentTab == 2"></hot>
			<view>
				<!--  笔记内容搜索-->
				<view>
					<tui-drawer mode="left" :visible="visiable" @close="closeDrawer">
						<view class="d-container">
							<view class="d-content">
								<view class="find-user">
									<navigator url="/pages/addfriend/addfriend">
										<view class="item">
											<tui-icon name="friendadd" size="20"></tui-icon>
											发现好友
										</view>
									</navigator>
								</view>
								<view class="d-function">
									<ul>
										<li>
											<navigator url="/pages/history/viewHistory">
												<view class="item">
													<tui-icon name="clock" size="20"></tui-icon>
													浏览记录
												</view>
											</navigator>
										</li>
										<li>
											<navigator url="/pages/group/group">
												<view class="item">
													<tui-icon name="like" size="20"></tui-icon>
													关注分组
												</view>
											</navigator>
										</li>
									</ul>
								</view>
								<view class="d-function">
									<ul>
										<li>
											<view class="item">
												<tui-icon name="circle" size="20"></tui-icon>
												其他功能
											</view>
										</li>
										<li>
											<view class="item">
												<tui-icon name="circle" size="20"></tui-icon>
												其他功能
											</view>
										</li>
									</ul>
								</view>
							</view>
						</view>
					</tui-drawer>
				</view>

				<view class="tui-content-box">
					<view class="tui-avatar-box">
						<view @click="openDrawer">
							<image :src="uAvatar" class="tui-avatar" mode="aspectFill" />
						</view>
					</view>
					<view class="tui-search-box" @click="toSearch">
						<tui-icon name="search-2" :size="18" color="#bfbfbf"></tui-icon>
						<view class="tui-search-text">请输入内容</view>
					</view>
				</view>





				<uv-waterfall ref="waterfall" v-model="list" :add-time="10" :left-gap="leftGap" :right-gap="rightGap"
					v-if="currentTab==1" :column-gap="columnGap" @changeList="changeList">
					<!-- 第一列数据 -->
					<template v-slot:list1>
						<!-- 为了磨平部分平台的BUG，必须套一层view -->
						<view>
							<view v-for="(item, index) in list1" :key="item.id" class="waterfall-item">
								<view class="waterfall-item__image" :style="[imageStyle(item)]"
									@click="toNote(item.id)">
									<image :src="item.coverFileName" mode="widthFix" :style="{width:item.width+'px'}">
									</image>
								</view>
								<view class="waterfall-item__ft" @click="toNote(item.id)">
									<view class="waterfall-item__ft__title">
										<text class="value">{{item.title}}</text>
									</view>
									<view class="waterfall-item__ft__desc uv-line-2">
										<image :src="item.pubUAvatar" mode="aspectFill"
											style="width: 30px;height: 30px;
											background: #fff;-webkit-animation: fadeinout 2s linear forwards;animation: fadeinout 2s linear forwards;" />
										<text class="value">{{item.pubUNickname}}</text>
									</view>
									<view style="display: flex;">
										<uv-icon style="margin-right: 17px;" name="eye" color="#6d6d6d" size="20"
											:label="item.viewCount"></uv-icon>
										<uv-icon name="heart-fill" v-if="item.isAgree==true" size="20"
											:label="item.agreeCount"></uv-icon>
										<uv-icon v-else name="heart" size="20" :label="item.agreeCount"></uv-icon>
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
								<view class="waterfall-item__image" :style="[imageStyle(item)]">
									<image :src="item.coverFileName" mode="widthFix" :style="{width:item.width+'px'}">
									</image>
								</view>
								<view class="waterfall-item__ft" @click="toNote(item.id)">
									<view class="waterfall-item__ft__title">
										<text class="value">{{item.title}}</text>
									</view>
									<view class="waterfall-item__ft__desc uv-line-2" @click="toNote(item.id)">
										<image :src="item.pubUAvatar" mode="aspectFill" style="width: 30px;height: 30px;
			background: #fff;-webkit-animation: fadeinout 2s linear forwards;animation: fadeinout 2s linear forwards;" />
										<text class="value">{{item.pubUNickname+' '}}</text>
									</view>
									<view style="display: flex;">
										<uv-icon style="margin-right: 17px;" name="eye" color="#6d6d6d" size="20"
											:label="item.viewCount"></uv-icon>
										<uv-icon name="heart-fill" v-if="item.isAgree==true" size="20"
											:label="item.agreeCount"></uv-icon>
										<uv-icon v-else name="heart" size="20" :label="item.agreeCount"></uv-icon>
									</view>
								</view>

							</view>
						</view>
					</template>
				</uv-waterfall>
			</view>

		</view>

	</view>
</template>

<script>
	import {
		guid
	} from '@/uni_modules/uv-ui-tools/libs/function/index.js'
	import {
		getUserInfo
	} from '@/api/v1/user.js'

	import {
		getNewBlogByPage
	} from '@/api/v1/note.js'

	import Interest from '@/pages/index/interest/interest.vue'
	import Hot from '@/pages/index/hot/hot.vue'
	import {
		loadImageEnd
	} from "@/utils/utils.js"

	export default {
		components: {
			Interest,
			Hot,
		},
		props: {
			seed: Number
		},
		data() {
			return {

				currentTab: 1,
				tabs: [{
						name: "关注"
					}, {
						name: "最新"
					},
					{
						name: "热榜"
					}
				],

				uAvatar: '',
				needRefresh: false,
				page: 1,
				pageSize: 5,
				total: 0,
				list: [],
				list1: [],
				list2: [],
				visiable: false,

				isEnd: false, //是否到底底部了
				loading: false, //是否正在加载
				leftGap: 10,
				rightGap: 10,
				columnGap: 10,

			}
		},
		watch: {
			// seed(newVal, oldVal) {
			// 	this.userInfo = uni.getStorageSync("userInfo")
			// },
		},
		onReachBottom() { //屏幕滚动时不断刷新数据
			this.getData();
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
			// this.getCategory()
			getUserInfo(uni.getStorageSync('userBasic').uuid).then(res => {
				if (res.success) {
					this.uAvatar = res.data.uAvatar
				}
			})

		},
		onLoad() {
			this.list = [];
			this.list1 = [];
			this.list2 = [];
			this.isEnd = false;
			this.total = 0;
			this.page = 1;
		},
		onShow() {


		},

		onHide() {

		},

		onTabItemTap(e) {
			this.currentTab = 1

			if (this.needRefresh) {
				uni.pageScrollTo({ // 回到顶部
					duration: 0,
					scrollTop: 0
				})
				this.onRefresh()
			} else {
				this.needRefresh = true
			}
		},

		methods: {
			toNote(blogId) {
				uni.navigateTo({
					url: '/pages/main/note?blogId=' + blogId
				});
			},

			change(e) {
				this.currentTab = e.index
			},
			changeList(e) {
				this[e.name].push(e.value);
			},
			toSearch() {
				uni.navigateTo({
					url: "/pages/index/search/search"
				})
			},

			getData() {
				let that = this;
				if (this.isEnd === true) {
					return;
				}
				this.isLoading = true;
				this.isEnd = false;

				getNewBlogByPage(this.page, this.pageSize).then(res => {
					if (res.success) {
						this.page = this.page + 1;
						if (this.page > res.data.pages) {
							this.isEnd = true;
						}
						this.list.push(...res.data.records);
						this.loading = false;
					}
				});
			},
			openDrawer() {
				this.visiable = true
			},
			closeDrawer() {
				this.visiable = false
			},


		}
	}
</script>

<style scoped>
	@import url(./index.css);
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