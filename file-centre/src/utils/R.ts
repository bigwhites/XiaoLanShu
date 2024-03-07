export class R {
  data: any;
  success: boolean;
  msg: string;
  datetime: Date;

  constructor(data: any, success: boolean, msg: string) {
    this.data = data;
    this.success = success;
    this.msg = msg;
    this.datetime = new Date();
  }

  static success(data: any = null, msg: string = 'ok'): R {
    return new R(data, true, msg);
  }

  static fail(msg: string, data: any = null): R {
    return new R(data, false, msg);
  }
}
