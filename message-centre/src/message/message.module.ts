import { Module } from '@nestjs/common';
import { WSMessageGateway } from './message.gateway';

@Module({ providers: [WSMessageGateway] })
export class MessageModule {}
