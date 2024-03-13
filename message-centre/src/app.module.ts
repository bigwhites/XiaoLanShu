import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
// import { EmailSendSubscriberModule } from './email-send-subscriber/email-send-subscriber.module.ts';
import { TypeOrmModule } from '@nestjs/typeorm';
import { WinstonModule } from 'nest-winston';
import { EmailSendSubscriberModule } from './email-send-subscriber/email-send-subscriber.module';
import * as winston from 'winston';
import 'winston-daily-rotate-file';
import { RedisCacheModule } from './config/redis-cache.module';
import { FileUploadModule } from './file-upload/file-upload.module';
import { MessageModule } from './message/message.module';
import { FollowMessage } from './Entity/FollowMessage';
import { WSMessageGateway } from './message/message.gateway';
import { FollowMessageService } from './message/service/FollowMessageService';

@Module({
  imports: [
    WinstonModule.forRoot({
      transports: [
        new winston.transports.DailyRotateFile({
          dirname: `logs`, // 日志保存的目录
          filename: '%DATE%.log', // 日志名称，占位符 %DATE% 取值为 datePattern 值。
          datePattern: 'YYYY-MM-DD', // 日志轮换的频率，此处表示每天。
          zippedArchive: true, // 是否通过压缩的方式归档被轮换的日志文件。
          maxSize: '3m', // 设置日志文件的最大大小，m 表示 mb 。
          maxFiles: '14d', // 保留日志文件的最大天数，此处表示自动删除超过 14 天的日志文件。
          // 记录时添加时间戳信息
          format: winston.format.combine(
            winston.format.timestamp({
              format: 'YYYY-MM-DD HH:mm:ss',
            }),
            winston.format.json(),
          ),
        }),
      ],
    }),

    TypeOrmModule.forRoot({
      type: 'postgres',
      logging: true,
      autoLoadEntities: true,
      // entities: [""],
      replication: {
        master: {
          host: 'localhost',
          port: 5433,
          username: 'postgres',
          password: '123456',
          database: 'message_centre',
        },
        slaves: [
          {
            host: 'localhost',
            port: 5434,
            username: 'postgres',
            password: '123456',
            database: 'message_centre',
          },
          {
            host: 'localhost',
            port: 5435,
            username: 'postgres',
            password: '123456',
            database: 'message_centre',
          },
        ],
      },
    }),
    // 引入实体
    // TypeOrmModule.forFeature([FollowMessage]),
    RedisCacheModule,
    EmailSendSubscriberModule,
    FileUploadModule,
    MessageModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
