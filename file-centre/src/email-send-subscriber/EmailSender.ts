// email.service.ts

import * as nodemailer from 'nodemailer';
import * as smtpTransport from 'nodemailer-smtp-transport';
import { Injectable } from '@nestjs/common';

export interface MailOptions {
  from: string;
  to: string | string[];
  subject: string;
  text?: string;
  html?: string;
}

@Injectable()
export class EmailSender {
  private transporter: nodemailer.Transporter;

  constructor() {
    const transporterConfig: smtpTransport.SmtpOptions = {
      host: 'smtp.qq.com',
      port: 465,
      secure: true, // 使用SSL
      auth: {
        user: '2632551453@qq.com', // 替换为你的QQ邮箱账号
        pass: 'mmtgzvliljjieccb', // 替换为你的授权码（不是邮箱密码）
      },
    };

    this.transporter = nodemailer.createTransport(
      smtpTransport(transporterConfig),
    );
  }

  public async sendMail(mailOptions: MailOptions): Promise<void> {
    try {
      mailOptions.from = '2632551453@qq.com';
      await this.transporter.sendMail(mailOptions);
      console.log('Email sent successfully');
    } catch (error) {
      console.error('Error occurred while sending email:', error);
    }
  }
}
