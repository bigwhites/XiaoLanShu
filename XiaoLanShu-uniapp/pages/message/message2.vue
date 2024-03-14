<template>
	<view class="content">
		<tui-navigation-bar backgroundColor="#fff" :isFixed="false" :isOpacity="false">
			<view class="nav">
				<tui-icon name="arrowleft" size="24"></tui-icon>
				<view>我的消息</view>
			</view>
		</tui-navigation-bar>

		<scroll-view scroll-y class="page" refresher-enabled="true" :refresher-triggered="triggered"
			@refresherrefresh="onRefresh">
			<view class="tabs">
				<view class="tabs-body">
					<view class="item " @click="next(1)">
						<tui-badge type="danger" class="tui-badge" absolute
							v-if="record.agreeCollectionCount > 0">{{ record.agreeCollectionCount }}</tui-badge>
						<view class="icon"><tui-icon name="like" color="#f90000"></tui-icon></view>
						<view class="name">赞和收藏</view>
					</view>

					<view class="item" @click="next(2)">
						<tui-badge type="danger" class="tui-badge" absolute
							v-if="record.addFollowCount > 0">{{ record.addFollowCount }}</tui-badge>
						<view class="icon"><tui-icon name="people" color="#00aa00"></tui-icon></view>
						<view class="name">新增关注</view>
					</view>
					<view class="item" @click="next(3)">
						<tui-badge type="danger" class="tui-badge" absolute
							v-if="record.noreplyCount > 0">{{ record.noreplyCount }}</tui-badge>
						<view class="icon"><tui-icon name="message" color="#ffaa00"></tui-icon></view>
						<view class="name">评论和@</view>
					</view>
				</view>
			</view>

			<view>
				<view class="container">

					<ul class="conversation-list" v-if="conversations.length > 0">
						<li class="conversation-item" :lineLeft="false" v-for="(conversation, key) in conversations"
							:key="key">
							<tui-swipe-action :operateWidth="140">
								<template v-slot:content>
									<view class="tui-item-box" @click="chat(conversation)"
										:style="topList.includes(conversation.userId)?{backgroundColor:'#f1f1f1'}:{backgroundColor:'#fff'}">
										<view class="tui-msg-box">
											<image :src="conversation.data.avatar" class="tui-msg-pic" mode="aspectFill"
												:lazy-load='true' />
											<view class="tui-msg-item">
												<view class="tui-msg-name">{{ conversation.data.name }}</view>
												<view class="tui-msg-content" v-if="!conversation.lastMessage.recalled">

													<text class="unread-text">
														{{ conversation.lastMessage.read === false && conversation.lastMessage.senderId === currentUser.id ? '[未读]' : '' }}
													</text>
													<text v-if="conversation.lastMessage.senderId === currentUser.id">我:
													</text>
													<text
														v-else>{{ conversation.type === 'group' ? conversation.lastMessage.senderData.name : conversation.data.name }}:
													</text>
													<text
														v-if="conversation.lastMessage.type === 'text'">{{ conversation.lastMessage.payload.text }}</text>
													<text
														v-else-if="conversation.lastMessage.type === 'video'">[视频消息]</text>
													<text
														v-else-if="conversation.lastMessage.type === 'audio'">[语音消息]</text>
													<text
														v-else-if="conversation.lastMessage.type === 'image'">[图片消息]</text>
													<text
														v-else-if="conversation.lastMessage.type === 'file'">[文件消息]</text>
													<text
														v-else-if="conversation.lastMessage.type === 'order'">[自定义消息:订单]</text>
													<text v-else>[[未识别内容]]</text>

												</view>
												<view class="tui-msg-content" v-else>
													<text>
														{{conversation.lastMessage.recaller.id === currentUser.id ? '你' : conversation.lastMessage.recaller.data.name}}撤回了一条消息
													</text>
												</view>
											</view>
										</view>
										<view class="tui-msg-right">
											<view class="tui-msg-time">
												{{ formatDate(conversation.lastMessage.timestamp) }}
											</view>
											<tui-badge type="danger" class="tui-badge"
												v-if="conversation.unread > 0">{{ conversation.unread }}</tui-badge>
										</view>
									</view>
								</template>

								<template v-slot:button>
									<view class="tui-custom-btn_box">
										<view class="tui-custom-btn-top" @click="customBtn(0,conversation)">
											{{conversation.top ? '取消置顶' : '置顶聊天' }}
										</view>
										<view class="tui-custom-btn-delete" @click="customBtn(1,conversation)">删除</view>
									</view>
								</template>

							</tui-swipe-action>

						</li>
					</ul>

					<view class="no-conversation" v-else>当前没有会话</view>
				</view>
			</view>
		</scroll-view>

	</view>
</template>

<script>
	import {
		getChatUserList,
		updateRecordCount,
		deleteRecord
	} from '@/api/chat.js'
	import {
		getUserRecord,
		clearUserRecord
	} from "@/api/user.js"
	import socket from 'plus-websocket'
	import {
		appConfig
	} from '@/config/config.js'
	import {
		timeAgo
	} from "@/utils/webUtils.js"
	import {
		formatDate
	} from '@/utils/utils.js';
	export default {
		data() {
			return {
				record: {
					agreeCollectionCount: 0,
					addFollowCount: 0,
					noreplyCount: 0,
				},


				triggered: false,

				unreadTotal: 0,
				conversations: [],

				actionPopup: {
					conversation: null,
					visible: false
				},
				currentUser: null,

				count: -1,

				actions: [{
						name: '收藏',
						width: 70,
						fontsize: 30,
						color: '#fff',
						background: '#C8C7CD'
					},
					{
						name: '删除',
						color: '#fff',
						fontsize: 30, //单位rpx
						width: 70, //单位px
						background: '#FD3B31'
					}
				],

				topList: [],

			}
		},

		watch: {

			record: {
				handler(newVal, oldVal) {

					if (newVal.agreeCollectionCount > 0 || newVal.addFollowCount > 0 || newVal.noreplyCount > 0 || this
						.count > 0) {
						uni.showTabBarRedDot({ //显示红点
							index: 2 //tabbar下标
						})
					} else {
						uni.hideTabBarRedDot({
							index: 2
						})
					}
				},

			},

		},
		created() {
			this.createWs()
		},

		onShow() {
			let cnt = Number(uni.getStorageSync('followCnt'));
			if (cnt != undefined && cnt != null) {
				this.record.addFollowCount = cnt;
			}
			// this.createWs()
		},

		beforeDestroy() {
			// this.goEasy.im.off(this.GoEasy.IM_EVENT.CONVERSATIONS_UPDATED, this.renderConversations);
		},

		methods: {
			//测试功能，测试navigateBack传递参数
			// getValue(currentTab){
			//     console.log(currentTab,'B页面传递的数据')
			// 	this.currentTab = currentTab
			// },
			//测试结束


			customBtn(index, conversation) {
				let that = this
				if (index == 0) {
					let description = conversation.top ? '取消置顶' : '置顶';
					this.goEasy.im.topConversation({
						conversation: conversation,
						top: !conversation.top,
						onSuccess: function() {
							uni.showToast({
								title: description + '成功',
								icon: 'none'
							});
							if (conversation.top) {
								that.topList.splice(that.topList.indexOf(conversation.userId), 1)
							} else {
								that.topList.push(conversation.userId)
							}
						},
						onFailed: function(error) {
							console.log(description, '失败：', error);
						}
					});

				} else {
					uni.showModal({
						content: '确认删除这条会话吗？',
						success: (res) => {
							if (res.confirm) {
								this.goEasy.im.removeConversation({
									conversation: conversation,
									onSuccess: function() {
										console.log('删除会话成功');
									},
									onFailed: function(error) {
										console.log(error);
									},
								});
							} else {
								//this.actionPopup.visible = false;
							}
						},
					})
				}
			},




			topConversation() { //会话置顶

				let conversation = this.actionPopup.conversation;
				let description = conversation.top ? '取消置顶' : '置顶';
				this.goEasy.im.topConversation({
					conversation: conversation,
					top: !conversation.top,
					onSuccess: function() {
						uni.showToast({
							title: description + '成功',
							icon: 'none'
						});
					},
					onFailed: function(error) {
						console.log(description, '失败：', error);
					}
				});
			},

			deleteConversation() {
				uni.showModal({
					content: '确认删除这条会话吗？',
					success: (res) => {
						if (res.confirm) {
							let conversation = this.actionPopup.conversation;
							this.actionPopup.visible = false;
							this.goEasy.im.removeConversation({
								conversation: conversation,
								onSuccess: function() {
									console.log('删除会话成功');
								},
								onFailed: function(error) {
									console.log(error);
								},
							});
						} else {
							this.actionPopup.visible = false;
						}
					},
				})
			},
			chat(conversation) {
				let path = '/pages/message/privateChat/privateChat?to=' + conversation.userId

				uni.navigateTo({
					url: path
				});
			},




			//-----------------------------------------------------------------------------------------

			onRefresh() {

				this.createWs()

				this.triggered = true;

				setTimeout(() => {
					this.triggered = false;
				}, 300)

				// this.getUserRecord()
				// this.loadConversations(); //加载会话列表

			},

			next(index) {

				if (index == 1) {
					uni.navigateTo({
						url: "/pages/message/message-agrees/message-agrees"
					})
					//清除所有的赞和收藏数量
				} else if (index == 2) {
					uni.navigateTo({
						url: "/pages/message/message-followers/message-followers",
					})
					//清除新关注数量
					this.record.addFollowCount = 0;
					uni.setStorageSync('followCnt', '0');
				} else {

					uni.navigateTo({
						url: "/pages/message/message-comments/message-comments"
					})
					//清除所有的回复
				}
				let params = {
					uid: uni.getStorageSync("userInfo").id,
					type: index
				}
				clearUserRecord(params).then(res => {
					this.getUserRecord()
				})
			},


			getUserRecord() {
				let params = {
					uid: uni.getStorageSync("userInfo").id
				}
				getUserRecord(params).then(res => {
					this.record = res.data
				})
			},


			acceptMessage(data) {

				let that = this

				let message = JSON.parse(data)

				if (Object.getPrototypeOf(message) != Array.prototype) {

					that.record = message
				}
			},

			//重新打开websocket
			createWs() {

				let that = this
				// if (uni.getStorageSync('socketIsOpen') === 'true') {
				// 	return;
				// 	console.log('is Opened,skip');
				// }
				uni.connectSocket({
					url: appConfig.WS_API + uni.getStorageSync("userBasic").uuid,
					header: {
						'content-type': 'application/json'
					},
					protocols: ['protocol1'],
					method: 'GET',
					complete: () => {}
				});
				uni.onSocketOpen((res) => {
					console.log('webSocket连接成功');
					uni.setStorage({
						key: 'socketIsOpen',
						data: 'true'
					})
				});
				uni.onSocketMessage((ress) => {
					let res = JSON.parse(ress.data);
					console.log(res)
					if (res.messageType === 2) { //关注
						this.record.addFollowCount = res.content + Number(uni.getStorageSync('followCnt'));
						uni.setStorageSync('followCnt', this.record.addFollowCount);
						plus.push.createMessage('通知', '有新的关注');
					}
				})




				// socket.connectSocket({
				// 	// ws://192.168.1.103:8083/platform/ws/
				// 	url: appConfig.WS_API + uni.getStorageSync("userBasic").uuid
				// });
				// socket.onSocketOpen(function(res) {
				// 	console.log('WebSocket连接已打开！');
				// 	console.log(res)
				// });
				// socket.onSocketError(function(res) {
				// 	console.log('WebSocket连接打开失败，请检查！');
				// });
				// socket.onSocketMessage(function(res) {
				// 	//uni.$emit('messageCount', { 'records': res.data })
				// 	console.log(res)
				// 	// that.acceptMessage(res)

				// });

			},

		},

	}
</script>

<style scoped>
	@import url(./message2.css);
</style>