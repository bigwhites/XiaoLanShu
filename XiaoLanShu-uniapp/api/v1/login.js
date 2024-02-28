import {
	request
} from '../../utils/request.js'
import {
	appConfig
} from '../../config/config.js'



export function loginByPwd(email,pwd) {
	email = email.replace(/\s/g, "");;
	return request.post(appConfig.USER_INFO+ "loginByPwd/"+email,pwd);
}

