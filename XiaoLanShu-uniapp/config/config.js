export const __WEB_API__ = 'api/v1/';

export let SERVER_IP = 'http://localhost:8078/';

export const appConfig = {

	tokenKey: 'AuthHeader',

	ImKey: 'BC-c7d655043ac7461fbf755a1f231725ff',

	// // 演示环境
	// WEB_API: 'http://192.168.50.83:8078/api/v1/',
	// WS_API: '192.168.100.10:88s02/api/platform/ws/',
	// USER_INFO: 'http:///192.168.50.83:8078/api/v1/userInfo/',
	// STATIC_PATH: 'http://192.168.50.83:8078/api/v1/messageCentre/',
	// BLOG_SERVER: 'http://192.168.50.83:8078/api/v1/blogServer/'

	// 开发环境
	WS_API: '192.168.100.10:88s02/api/platform/ws/',
	USER_INFO: SERVER_IP + __WEB_API__ + 'userInfo/',
	STATIC_PATH: SERVER_IP + __WEB_API__ + 'messageCentre/',
	BLOG_SERVER: SERVER_IP + __WEB_API__ + 'blogServer/'
};