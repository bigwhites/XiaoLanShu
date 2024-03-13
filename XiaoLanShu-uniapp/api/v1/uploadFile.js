import {
	appConfig
} from "../../config/config";
import {
	request
} from "../../utils/request";

export function reqUploadFile(uuid, fileName, type) {
	return request.post(appConfig.USER_INFO + 'updateCoverAvaTar', {
		uuid: uuid,
		type: type,
		oriFileName: fileName
	})
}