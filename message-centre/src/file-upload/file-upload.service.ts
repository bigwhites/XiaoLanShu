import { Inject, Injectable } from '@nestjs/common';
import * as amqp from 'amqplib';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';
import * as fs from 'fs';
import { FileDto } from './DTO/FileDto';
import * as AppConfig from '../config/AppConfig';

@Injectable()
export class FileUploadService {
  private channel: amqp.Channel;

  constructor(
    @Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger,
  ) {}

  public async connectToQueue(): Promise<void> {
    const connection = await amqp.connect('amqp://root:123456@localhost:5672');
    this.channel = await connection.createChannel();

    // 声明或检查队列存在
    await this.channel.assertQueue('DeleteFileQueue', { durable: true });

    // 开始监听队列
    this.listenForMessages();
  }

  private listenForMessages(): void {
    this.channel.consume(
      'DeleteFileQueue',
      async (msg: amqp.ConsumeMessage | null) => {
        if (msg !== null) {
          const messageContent = msg.content;
          const fileDto: FileDto = JSON.parse(messageContent.toString());
          const filePath =
            AppConfig.FILE_ROOT +
            '\\' +
            fileDto.filePath +
            '\\' +
            fileDto.fileName;

          const ackMessage = (): void => {
            // 确认消息已被处理
            this.channel.ack(msg);
          };

          if (fs.existsSync(filePath)) {
            this.logger.info(`文件${filePath}存在`);
            fs.unlink(filePath, ackMessage);
          }
        }
      },
      { noAck: false },
    );
  }

  public closeConnection(): void {
    if (this.channel && this.channel.connection) {
      this.channel.close();
      this.channel.connection.close();
    }
  }
}
