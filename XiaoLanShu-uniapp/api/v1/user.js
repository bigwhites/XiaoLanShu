import {
	request,
} from '../../utils/request.js';
import {
	appConfig,
} from '../../config/config.js';


export function getUserInfo(uuid) {
	const url = appConfig.USER_INFO + 'userDetail/' + uuid;
	return request.get(url, null);
}

export function searchUsers(keyword) {
	const url = appConfig.USER_INFO + 'searchUserByTypes';
	return request.get(url, keyword);
}

export function changeFollowStatus(fromId, toId) {
	const url = appConfig.USER_INFO + 'changeFollowStatus';
	return request.post(url, {
		fromUuid: fromId,
		toUuid: toId
	})
}

export function getBlogPageByUser(curPage, pageSize, uuid) {
	const url = appConfig.BLOG_SERVER + 'blog/getByPid/' + curPage + '/' + pageSize + '/' +
		uuid;
	return request.post(url);
}

export function updateUserDetail(userDeatilItem) {
	const url = appConfig.USER_INFO + 'updateUDetail';
	return request.post(url, userDeatilItem);

}

export function isFollow(fromId, toId) {
	const url = appConfig.USER_INFO + 'isFollow'
	let params = {
		fromUuid: fromId,
		toUuid: toId
	};
	return request.post(url, params);
}