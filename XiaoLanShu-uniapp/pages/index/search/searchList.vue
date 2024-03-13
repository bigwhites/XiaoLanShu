<template>
	<view class="container">

		<tui-navigation-bar backgroundColor="#fff" :isFixed="false" :isOpacity="false">
			<view class="tui-content-box">
				<view class="tui-avatar-box">
					<view @click="back"><tui-icon name="arrowleft" size="25"></tui-icon></view>
				</view>
				<view class="tui-search-box">
					<tui-icon name="search-2" :size="18" color="#bfbfbf"></tui-icon>

					<input class="tui-search-text" placeholder="请输入搜索内容" v-model="keyword" />
				</view>
				<view class="tui-notice-box">
					<tui-button type="danger" height="54rpx" width="100rpx" :size="24" @click="search()">搜索</tui-button>
				</view>
			</view>
		</tui-navigation-bar>

		<nav class="navbar">
			<ul class="nav-list">
				<li class="nav-item dropdown">
					<a href="#" :class="0==nav?'nav-link activated':'nav-link'" @click="selectNav(0)">
						{{typeName}}
						<tui-icon name="arrowup" :size="18" v-if="showMenu"></tui-icon>
						<tui-icon name="arrowdown" :size="18" v-else></tui-icon>
					</a>
					<ul class="dropdown-menu" v-show="showMenu">
						<!-- <li :class="0==activate?'menu-activated':''"><a href="#"
								@click="esSearch(1,false)"></a></li> -->
						<li :class="1==activate?'menu-activated':''"><a href="#" @click="esSearch(1,false)">最热</a></li>
						<li :class="2==activate?'menu-activated':''"><a href="#" @click="esSearch(2,false)">最新</a></li>
					</ul>
				</li>
				<li class="nav-item">
					<a href="#" :class="1==nav?'nav-link activated':'nav-link'" @click="selectNav(1)">用户</a>
				</li>
				<!-- <li class="nav-item">
					<a href="#" :class="2==nav?'nav-link activated':'nav-link'" @click="selectNav(2)">表情包</a>
				</li> -->
			</ul>
		</nav>

		<view>
			<!--  笔记内容搜索-->
			<uv-waterfall ref="waterfall" v-model="list" :add-time="10" :left-gap="leftGap" :right-gap="rightGap"
				:column-gap="columnGap" @changeList="changeList">
				<!-- 第一列数据 -->
				<template v-slot:list1>
					<!-- 为了磨平部分平台的BUG，必须套一层view -->
					<view>
						<view v-for="(item, index) in list1" :key="item.id" class="waterfall-item">
							<view class="waterfall-item__image" :style="[imageStyle(item)]" @click="toNote(item.id)">
								<image :src="item.coverFileName" mode="widthFix" :style="{width:item.width+'px'}">
								</image>
							</view>
							<view class="waterfall-item__ft" @click="toNote(item.id)">
								<view class="waterfall-item__ft__title">
									<text class="value">{{item.title}}</text>
								</view>
								<view class="waterfall-item__ft__desc uv-line-2">
									<image :src="item.pubUAvatar" mode="aspectFill" style="width: 30px;height: 30px;
										background: #fff;-webkit-animation: fadeinout 2s linear forwards;animation: fadeinout 2s linear forwards;" />
									<text class="value">{{item.pubUNickname}}</text>
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

							</view>

						</view>
					</view>
				</template>
			</uv-waterfall>
		</view>

	</view>
</template>

<script>
	import {
		searchNotesPage
	} from "@/api/v1/searchNote.js"
	import {
		addBrowseRecord
	} from "@/api/browseRecord.js"
	import {
		esSearch
	} from '@/api/search.js'
	import {
		addSearchRecord
	} from "@/api/searchRecord.js"
	import {
		searchUser
	} from '@/api/user.js'
	import {
		clearFollow,
		followUser
	} from '@/api/follow.js'
	import {
		guid
	} from '@/uni_modules/uv-ui-tools/libs/function/index.js'
	export default {

		data() {
			return {
				triggered: false,
				userInfo: {}, //有自己uuid

				page: 1,
				pageSize: 5,
				total: 0,
				list: [],
				list1: [],
				list2: [],

				isEnd: false, //是否到底底部了
				loading: false, //是否正在加载
				keyword: '',

				leftGap: 10,
				rightGap: 10,
				columnGap: 10,


				// 导航栏,
				nav: 0,
				showMenu: false,
				clickCount: 0,
				activate: 2,
				typeName: '笔记',
				type: 2,
				// intId: null,

				//查找用户
				userList: [],
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
			this.userInfo = uni.getStorageSync("userBasic")

		},
		onLoad(options) {
			this.keyword = options.keyword
			this.esSearch(-1, true) //默认为最新
		},
		onReachBottom() {
			this.loadData();
		},

		methods: {
			back() {

				uni.reLaunch({
					url: '/pages/index/index'
				})
			},

			selectNav(index) {

				if (index == 0) {
					this.showMenu = !this.showMenu
					// this.esSearch(2, false)
				} else if (index == 1) {
					//搜索用户
					this.showMenu = false

					this.searchUser()

				} //else {
				// 	this.showMenu = false
				// }

				this.nav = index
			},
			changeList(e) {
				this[e.name].push(e.value);
			},



			loadData() {
				let that = this
				if (that.isEnd == true) {
					return;
				}
				that.isEnd = false
				that.loading = true
				let parms = {
					page: that.page,
					pageSize: that.pageSize,
					keyword: that.keyword
				};
				searchNotesPage(parms, that.type).then(res => {
					if (res.success) {
						this.page += 1;
						if (this.page > res.data.pages) {
							this.isEnd = true;
						}
						// console.log(res.data.records)
						this.list.push(...res.data.records);
						this.loading = false;
					}
				});
			},
			getImgInfo(e) {

				//添加一条浏览记录
				let data = {}
				data.uid = this.userInfo.id
				data.mid = e.id

				addBrowseRecord(data).then(res => {
					uni.navigateTo({
						url: "/pages/main/main?mid=" + e.id
					})
				})
			},
			toNote(blogId) {
				uni.navigateTo({
					url: '/pages/main/note?blogId=' + blogId
				});
			},

			search() {
				//搜索全部数据
				this.isEnd = false;
				if (this.nav == 0) {
					this.esSearch(this.type, false)
				} else if (this.nav == 1) {
					this.searchUser()
				} else {
					//搜索表情包
				}

			},

			esSearch(type, show) {
				if (type == -1) {
					this.type = 2;
					this.loadData();
					return;
				}
				this.activate = type
				if (type === 1) {
					this.typeName = '最热'
				} else {
					this.typeName = '最新'
				}
				setTimeout(() => {
					this.showMenu = false;
				}, 12)
				this.list = [];
				this.list1 = [];
				this.list2 = [];
				this.page = 1;
				this.total = 0;
				this.loadData();

			},

			searchUser() {

				let params = {
					keyword: this.keyword,
					uid: this.userInfo.id
				}

				searchUser(this.page, this.limit, params).then(res => {

					this.userList = res.data.records
					this.total = res.data.total

				})
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


			follow(fid, index) {

				let followDTo = {}
				followDTo.uid = this.userInfo.id
				followDTo.fid = fid
				//添加关注
				followUser(followDTo).then(res => {
					this.userList[index].isfollow = true
				})
			},

			clearFollow(fid, index) {


				let followDTo = {}
				followDTo.uid = this.userInfo.id
				followDTo.fid = fid
				clearFollow(followDTo).then(res => {
					this.userList[index].isfollow = false
				})
			}

		}
	}
</script>

<style scoped>
	@import url(./searchList.css);
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