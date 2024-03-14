import { Inject, Injectable } from '@nestjs/common';
import * as amqp from 'amqplib';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';
import { WSMessageGateway } from '../message.gateway';
import { FollowMessageService } from './message.followMessage.service';
import { FollowMessage } from '../../Entity/FollowMessage';
import { MessageEvent, MessageType } from '../DTO/WSMessageResponse';

@Injectable()
export class MessageMqListenerService {
  private channel: amqp.Channel;

  constructor(
    @Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger,
    private readonly wSMessageGateway: WSMessageGateway,
    private readonly followMessageService: FollowMessageService,
  ) {
  }

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

  private listenFollowQueue(): Promise<void> {
    return this.channel.consume(
      'FOLLOW_QUEUE',
      async (msg: amqp.ConsumeMessage | null) => {
        if (msg != null) {
          const content = JSON.parse(msg.content.toString());
          this.logger.info(content);
          //将消息内容存入数据库
          const followMessage = new FollowMessage();
          followMessage.fromUuid = content.fromUuid;
          followMessage.toUuid = content.toUuid;
          followMessage.messageTime = new Date();
          followMessage.avatar = content.fromAvatar;
          followMessage.nickname = content.fromNickname;
          //判断用户是否在线，如果在线就直接发送消息，否则就将消息存入数据库，待用户上线后再发送
          let isSend = false;
          if (this.wSMessageGateway.isUserOnline(content.toUuid)) {
            isSend = true;
            this.logger.debug('发送关注消息成功');
            await this.wSMessageGateway.sendMessage(
              1,
              content.toUuid,
              MessageEvent.FOLLOW,
              MessageType.FOLLOW,
              // eslint-disable-next-line @typescript-eslint/no-unused-vars
              (err) => {
                this.logger.debug('用户掉线，无法发送');
                isSend = false;
              },
            );
          }

          followMessage.status = isSend ? '1' : '0';
          await this.followMessageService.create(followMessage);
          this.channel.ack(msg);
        }
      },
      { noAck: false },
    );
  }
}
