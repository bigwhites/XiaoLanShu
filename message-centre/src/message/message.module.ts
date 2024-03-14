import {
  Module,
  OnApplicationBootstrap,
  OnModuleDestroy,
} from '@nestjs/common';
import { WSMessageGateway } from './message.gateway';
import { FollowMessageService } from './service/message.followMessage.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { FollowMessage } from '../Entity/FollowMessage';
import { MessageMqListenerService } from './service/message.mqListener.service';

@Module({
  imports: [TypeOrmModule.forFeature([FollowMessage])],
  providers: [WSMessageGateway, MessageMqListenerService, FollowMessageService], // 导出以可被其它模块使用
  exports: [FollowMessageService, WSMessageGateway],
})
export class MessageModule implements OnApplicationBootstrap, OnModuleDestroy {
  constructor(private readonly mqService: MessageMqListenerService) {}

  async onApplicationBootstrap() {
    await this.mqService.connectToQueue();
  }

  async onModuleDestroy() {
    this.mqService.closeConnection();
  }
}
