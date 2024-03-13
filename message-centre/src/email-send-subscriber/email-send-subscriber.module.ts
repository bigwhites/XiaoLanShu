// app.module.ts
import {
  Module,
  OnApplicationBootstrap,
  OnModuleDestroy,
} from '@nestjs/common';
import { EmailSendSubscriberService } from './email-send-subscriber.service';
import { EmailSender } from './EmailSender';

@Module({
  providers: [EmailSendSubscriberService, EmailSender],
})
export class EmailSendSubscriberModule
  implements OnModuleDestroy, OnApplicationBootstrap
{
  constructor(private readonly rabbitMQService: EmailSendSubscriberService) {}

  async onApplicationBootstrap() {
    await this.rabbitMQService.connectToQueue();
  }

  async onModuleDestroy() {
    this.rabbitMQService.closeConnection();
  }
}
