import { Inject, Injectable } from '@nestjs/common';
import * as amqp from 'amqplib';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';
import * as fs from 'fs';
import { FileDto } from './DTO/FileDto';
import * as AppConfig from '../config/AppConfig';
import { InjectRedis } from '@nestjs-modules/ioredis';
import { Redis } from 'ioredis';

@Injectable()
export class FileUploadService {
  private channel: amqp.Channel;

  constructor(
    @Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger,
    @InjectRedis() private redisClient: Redis,
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
            fs.unlinkSync(filePath);
            ackMessage();
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

  async uploadMultiple(files: Array<Express.Multer.File>, redisKey: string) {
    if ((await this.redisClient.get('usedFT:' + redisKey)) == '1') {
      throw new Error('请勿重复上传');
    }
    const fileNamesList: string[] = await this.redisClient.lrange(
      redisKey,
      0,
      -1,
    );
    if (fileNamesList === null || fileNamesList.length === 0) {
      throw new Error('上传未经许可的文件');
    }
    const path = AppConfig.FILE_ROOT + '\\' + fileNamesList[0];
    if (!fs.existsSync(path)) {
      fs.mkdirSync(path, { recursive: true });
      this.logger.info('创建文件夹成功');
    }
    let i: number = 1;
    for (const file of files) {
      const fileName = path + '\\' + fileNamesList[i];
      ++i;
      fs.writeFile(fileName, file.buffer, (err) => {
        this.redisClient.set('usedFT:' + redisKey, '1', 'EX', 60 * 5);
        if (err) {
          throw new Error('err');
        }
      });
    }
  }

  async uploadFilesSingle(
    file: Express.Multer.File,
    redisKey: string,
    index: number,
  ) {
    if ((await this.redisClient.get('usedFT:' + redisKey)) == '1') {
      throw new Error('请勿重复上传');
    }
    const fileCount = (await this.redisClient.llen(redisKey)) - 1;
    if (fileCount < index) {
      throw new Error('上传未经许可的文件');
    }
    let filePath: string = AppConfig.FILE_ROOT + '\\';
    filePath += (await this.redisClient.lrange(redisKey, 0, 0))[0];
    filePath += '\\';

    const fileName: string = (
      await this.redisClient.lrange(redisKey, index, index)
    )[0];
    //index从1 开始计数，0为路径
    if (!fs.existsSync(filePath)) {
      fs.mkdirSync(filePath, { recursive: true });
    }
    fs.writeFile(filePath + fileName, file.buffer, (err) => {
      if (err) {
        throw new Error('err');
      }
      if (index === fileCount) {
        this.redisClient.set('usedFT:' + redisKey, '1', 'EX', 60 * 5);
      }
    });
  }
}
