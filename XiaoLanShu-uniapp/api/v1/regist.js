import {
  request
} from '../../utils/request.js'
import {
  appConfig
} from '../../config/config.js'

const urlBasic = appConfig.USER_INFO;

export function sendValidCode(data){
  return request.post(urlBasic+"sendRegEmail",data);
}

export function register(data){
  return request.post(urlBasic+'register',data);
  // return request.post()
}