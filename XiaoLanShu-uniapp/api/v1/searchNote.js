import {
	request
} from '../../utils/request.js'
import {
	appConfig
} from '../../config/config.js'

// 1.按照时间顺序
export function searchNotesPage(parms, searchType) {
	const url = appConfig.BLOG_SERVER + 'search/pageByKeyword';
	// if (type === 2) { //时间顺序，最新优先
	return request.get(url, parms);
	// }
}