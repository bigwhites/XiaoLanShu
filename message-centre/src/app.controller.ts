import { Controller, Get, Inject } from '@nestjs/common';
import { AppService } from './app.service';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';
import { FollowMessageService } from './message/service/FollowMessageService';
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
    // // console.log('console is work');
    // // this.logger.error('ssss');
    // this.wSMessageGateway.sendMessage('djihscokcjs', '/sss');
    // return this.appService.getHello();
    const findAll = await this.followMessageService.findAll();
    console.log(findAll);
    // return findAll.toString();
    return 'Hello World!';
  }
}
