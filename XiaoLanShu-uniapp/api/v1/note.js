import {
	request,
} from '../../utils/request.js';
import {
	appConfig,
} from '../../config/config.js';

export function getOneNoteById(blogId, uuid) {
	return request.get(appConfig.BLOG_SERVER + 'blog/getNoteByBId', {
		blogId: blogId,
		viewUuid: uuid
	});
}

export function getViewHistory(page, pageSize, uuid) {
	const url = appConfig.BLOG_SERVER + 'blog/getHistoryByVId/' + page + '/' + pageSize + '/' + uuid;
	return request.get(url);
}

export function getNewBlogByPage(page, pageSize) {

	const url = appConfig.BLOG_SERVER + 'blog/getNewByPage';
	return request.get(url, {
		page: page,
		pageSize: pageSize
	});
}

export function changeAgree(blogId, viewUuid) {
	let parms = {
		blogId: blogId,
		operation: 1
	};
	const url = appConfig.BLOG_SERVER + 'blog/agreeOrCollect/' + viewUuid
	return request.get(url, parms);
}

export function changeCollection(blogId, viewUuid) {
	let parms = {
		blogId: blogId,
		operation: 2
	};
	const url = appConfig.BLOG_SERVER + 'blog/agreeOrCollect/' + viewUuid
	return request.get(url, parms);
}

export function collectionHistory(viewUuid, page, pageSize) {
	let parms = {
		viewUuid: viewUuid,
		page: page,
		pageSize: pageSize
	};
	const url = appConfig.BLOG_SERVER + '/blog/collectionHistoryByVId';
	return request.get(url, parms);
}