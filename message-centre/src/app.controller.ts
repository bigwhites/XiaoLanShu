import { Controller, Get, Inject } from '@nestjs/common';
import { AppService } from './app.service';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';
import { FollowMessageService } from './message/service/message.followMessage.service';
import { WSMessageGateway } from './message/message.gateway';

@Controller('/hello')
export class AppController {
  constructor(
    private readonly appService: AppService,
    @Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger,
    private readonly wSMessageGateway: WSMessageGateway,
    private readonly followMessageService: FollowMessageService,
  ) {}

  @Get()
  async getHello(): Promise<string> {
    await this.wSMessageGateway.sendMessage(
      'send a data',
      '8325e58a3884b7ec1a08d0636c8f130c',
      'ssss',
      2,
      () => {},
    );
    // return findAll.toString();
    return 'Hello World!';
  }
}
