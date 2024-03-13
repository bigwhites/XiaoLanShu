<template>
	<view class="container">
		<tui-navigation-bar backgroundColor="#fff" :isFixed="false" :isOpacity="false">
			<view class="nav">
				<view class="n-left">
					<view @click="back"><tui-icon name="arrowleft" size="24"></tui-icon></view>
					<view>用户专辑</view>
				</view>
			</view>
		</tui-navigation-bar>
		<view class="main">
			<view class="albums">
				<ul>
					<li :class="T == index ? 'item-activate' : ''" v-for="(item, index) in dataList" :key='index'>
						<view @click="selectOne(item.id, index)" class="album-body">
							<image :src="item.cover" mode="aspectFill" />
							<view class="content">{{ item.name }}</view>
						</view>
					</li>
				</ul>
			</view>
			<view class="add-album">
				<view class="title">点击快速创建专辑</view>
				<view class="add-album-body">
					<input class="add-input" placeholder="请输入专辑名称 " v-model="album.name" />
					<tui-button type="danger" height="66rpx" width="120rpx" @click="saveAlbum"
						:size="24">创建专辑</tui-button>
				</view>
			</view>
		</view>
		<view class="fotter">
			<view class="add">
				<tui-button type="danger" shape="circle" @click="publish" v-if="mid == ''"
					:disabled="isDisabled">点击发布</tui-button>
				<tui-button type="danger" shape="circle" @click="saveImgToAlbum" v-else>添加至专辑</tui-button>
			</view>
		</view>
		<tui-toast ref="toast"></tui-toast>
	</view>
</template>

<script>
	import {
		saveAlbum,
		getAllAlbum,
		addAlbumImgRelation
	} from "@/api/album.js"
	import {
		// publish,
		updateImgDetail,
		updateStatus
	} from '@/api/imgDetail.js'

	import {
		addBlog,
	} from '@/api/v1/addBlog.js';
	import {
		appConfig
	} from '@/config/config'
	import {
		isURL
	} from '@/utils/validate.js'
	export default {
		data() {
			return {
				T: -1,
				imgInfo: {
					albumId: '',
				},
				album: {},
				dataList: [],
				userInfo: {},
				imageList: [],
				imgurl: [],
				mid: '',
				isDisabled: false,
				type: '',
			}
		},
		onLoad(option) {
			if (typeof option.mid != 'undefined' && option.mid != null) {
				this.mid = option.mid
			}

			if (option.type != null) {
				this.type = option.type
			}

		},
		created() {
			let that = this
			// that.userInfo = uni.getStorageSync("userInfo")

			if (typeof that.userInfo == 'undefined' || that.userInfo == null || that.userInfo == '') {
				return
			} else {
				that.getAllAlbum()
			}
		},
		methods: {

			back() {
				uni.navigateBack({
					delta: 1
				})
			},
			getAllAlbum() {
				let data = {
					uid: this.userInfo.id
				}
				getAllAlbum(data).then(res => {
					this.dataList = res.data
				})
			},

			selectOne(id, index) {
				this.T = index
				this.imgInfo.albumId = id
			},
			publish() {
				let that = this
				that.imgInfo = JSON.parse(uni.getStorageSync('imgInfo'))
				if (false) {
					let params = {
						title: "请选择一份专辑",
					}
					that.$refs.toast.show(params);
				} else {
					that.isDisabled = true
					that.imageList = JSON.parse(uni.getStorageSync("imgList"));
					uni.showLoading({
						title: '上传中...'
					});

					// that.imgurl = that.imageList.filter(item => !isURL(item)).map((value, index) => {
					// 	return {
					// 		name: 'files',
					// 		uri: value
					// 	};
					// });



					addBlog(that.imgInfo, that.imageList).then(res => {


						setTimeout(() => {
							uni.hideLoading();
						}, 1024);

						uni.showToast({
							title: '上传成功',

						});

						setTimeout(() => {
							that.isDisabled = false
							uni.reLaunch({
								url: '/pages/user/user'
							})
						}, 1000);



					});



				}


				uni.removeStorageSync("imgList")
				uni.removeStorageSync("tags")
				uni.removeStorageSync("imgInfo")
			},


			saveAlbum() {

				this.album.cover =
					"https://cc-video-oss.oss-accelerate.aliyuncs.com/2023/03/20/4bae43e216124cf1966a474594a81612img_nodata.png"
				this.album.uid = this.userInfo.id
				saveAlbum(this.album).then(res => {
					let params = {
						title: "添加成功",
						imgUrl: "/static/images/toast/check-circle.png",
						icon: true
					}
					this.$refs.toast.show(params);
					this.album.name = ''
					this.getAllAlbum(this.album.uid)
				})
			},

			saveImgToAlbum() {
				let albumImgRelationDTO = {}
				albumImgRelationDTO.uid = this.userInfo.id
				albumImgRelationDTO.aid = this.imgInfo.albumId
				albumImgRelationDTO.mid = this.mid
				addAlbumImgRelation(albumImgRelationDTO).then(res => {
					let params = {
						title: "收藏成功",
						imgUrl: "/static/images/toast/check-circle.png",
						icon: true
					}
					this.$refs.toast.show(params);

					uni.navigateTo({
						url: "/pages/main/main?mid=" + this.mid
					})
				})
			}
		}
	}
</script>

<style scoped>
	@import url(./addalbum.css);
</style>