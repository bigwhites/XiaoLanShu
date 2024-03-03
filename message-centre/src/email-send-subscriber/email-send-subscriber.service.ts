// rabbitmq.service.ts
import { Inject, Injectable } from '@nestjs/common';
import * as amqp from 'amqplib';
import { Logger } from 'winston';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import * as emailSender from './EmailSender';
import { Redis } from 'ioredis';
import { InjectRedis } from '@nestjs-modules/ioredis';
import { VerificationCodeUtil } from '../utils/VerificationCodeUtil';

@Injectable()
export class EmailSendSubscriberService {
  private channel: amqp.Channel;
  private sender: emailSender.EmailSender;

  constructor(
    @Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger,
    @InjectRedis()
    private redisClient: Redis,
  ) {
    this.sender = new emailSender.EmailSender();
  }

  public async connectToQueue(): Promise<void> {
    const connection = await amqp.connect('amqp://root:123456@localhost:5672');
    this.channel = await connection.createChannel();

    // 声明或检查队列存在
    await this.channel.assertQueue('MailSendQueue', { durable: true });

    // 开始监听队列
    this.listenForMessages();
  }

  private listenForMessages(): void {
    this.channel.consume(
      'MailSendQueue',
      async (msg: amqp.ConsumeMessage | null) => {
        if (msg !== null) {
          const messageContent = msg.content;
          const userEmail = JSON.parse(messageContent.toString()).userEmail;

          this.logger.info(userEmail);
          const validCode = VerificationCodeUtil.generateDigitCode(6);
          const mailOptions: emailSender.MailOptions = {
            from: null,
            to: '2770385913@qq.com',
            subject: '[小蓝书平台] 您的验证码',
            html:
              '<h1>欢迎注册小蓝书!</h1>' +
              '<p>您的验证码如下：' +
              validCode +
              '。</p>',
          };
          // //gen valid code
          // await this.sender.sendMail(mailOptions);
          await this.redisClient.set(userEmail, validCode);
          this.logger.info('send a code 2 ' + userEmail + 'as' + validCode);
          this.redisClient.expire(userEmail, 5 * 60);
        }
        // 确认消息已被处理
        this.channel.ack(msg);
      },
      { noAck: false },
    );
  }

  // 在应用关闭时断开与RabbitMQ的连接
  public closeConnection(): void {
    if (this.channel && this.channel.connection) {
      this.channel.close();
      this.channel.connection.close();
    }
  }
}
