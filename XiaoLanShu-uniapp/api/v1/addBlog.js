import {
	request
} from '../../utils/request.js'
import {
	appConfig
} from '../../config/config.js'

export function addBlog(imgInfo, imgList) {
	let req = {};
	req.title = imgInfo.title;
	req.content = imgInfo.content;
	req.pubUuid = imgInfo.pubUuid;
	req.imgCount = imgList.length;
	const url1 = appConfig.BLOG_SERVER + 'blog/publish';

	return new Promise(callback => {
		request.post(url1, req).then(
			res => {
				if (res.success) {
					const token = res.data.nameKey;
					let i = 0;
					imgList.forEach(img => {
						++i;
						console.log(token)
						uni.uploadFile({
							url: appConfig.STATIC_PATH + 'fileUpload/multiple/single',
							name: 'file',
							header: {
								rtoken: token,
								index: i
							},
							filePath: img
						}).then(res => {
							if (i === imgList.length) {
								callback(res);
							}
						});
					});
				}
			});

	})
}