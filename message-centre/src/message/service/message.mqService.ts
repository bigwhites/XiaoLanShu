import { Inject, Injectable } from '@nestjs/common';
import * as amqp from 'amqplib';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';

@Injectable()
export class MessageMqService {
  private channel: amqp.Channel;

  constructor(
    @Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger,
  ) {}

  public async connectToQueue(): Promise<void> {
    const connection = await amqp.connect('amqp://root:123456@localhost:5672');
    this.channel = await connection.createChannel();

    // 声明或检查队列存在
    await this.channel.assertQueue('FOLLOW_QUEUE', { durable: true });

    // 开始监听队列
    this.listenFollowQueue();
  }

  public closeConnection(): void {
    if (this.channel && this.channel.connection) {
      this.channel.close();
      this.channel.connection.close();
    }
  }

  private listenFollowQueue(): void {
    this.channel.consume(
      'FOLLOW_QUEUE',
      async (msg: amqp.ConsumeMessage | null) => {
        if (msg != null) {
          const content = JSON.parse(msg.content.toString());
          this.logger.info(content);
          this.channel.ack(msg);
        }
      },
      { noAck: false },
    );
  }
}
