// 验证码工具类
export class VerificationCodeUtil {
  /**
   * 生成一个length位随机数字验证码
   */
  static generateDigitCode(length: number): string {
    const digits = '0123456789';
    let code = '';
    for (let i = 0; i < length; i++) {
      const index = Math.floor(Math.random() * digits.length);
      code += digits[index];
    }
    return code;
  }
}
