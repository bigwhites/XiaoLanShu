import {
  Module,
  OnApplicationBootstrap,
  OnModuleDestroy,
} from '@nestjs/common';
import { WSMessageGateway } from './message.gateway';
import { MessageMqService } from './service/message.mqService';
import { FollowMessageService } from './service/FollowMessageService';
import { TypeOrmModule } from '@nestjs/typeorm';
import { FollowMessage } from '../Entity/FollowMessage';

@Module({
  imports: [TypeOrmModule.forFeature([FollowMessage])],
  providers: [WSMessageGateway, MessageMqService, FollowMessageService], // 导出以可被其它模块使用
  exports: [FollowMessageService, WSMessageGateway],
})
export class MessageModule implements OnApplicationBootstrap, OnModuleDestroy {
  constructor(private readonly mqService: MessageMqService) {}

  async onApplicationBootstrap() {
    await this.mqService.connectToQueue();
  }

  async onModuleDestroy() {
    this.mqService.closeConnection();
  }
}
