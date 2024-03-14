import {
  WebSocketServer,
  WebSocketGateway,
  OnGatewayDisconnect,
} from '@nestjs/websockets';
import { Server, WebSocket } from 'ws';
import { Inject, Injectable, OnModuleInit } from '@nestjs/common';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';
import { WSMessageResponse } from './DTO/WSMessageResponse';

@Injectable()
@WebSocketGateway(3001)
export class WSMessageGateway implements OnModuleInit, OnGatewayDisconnect {
  @WebSocketServer()
  server: Server;
  map: Map<string, WebSocket>;

  constructor(
    @Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger,
  ) {
    this.map = new Map();
  }

  handleDisconnect(client: any) {
    this.logger.debug(client);
  }

  onModuleInit() {
    this.server.on('connection', (ws, request) => {
      this.logger.info(request.url + '连接成功');
      this.map.set(request.url.substr(1), ws); //存入当前用户

      ws.on('message', (data: WSMessageResponse) => {
        this.handleMessage(data);
      });
    });
  }

  async sendMessage(
    message: any,
    id: string,
    event: string,
    messageType: number,
    onSendErr: any,
  ) {
    if (!this.map.has(id)) {
      this.logger.info('用户不在线上或掉线');
      onSendErr(new Error('用户不在线上或掉线'));
      return;
    }
    const ws: WebSocket = this.map.get(id);
    const wsMessageResponse = new WSMessageResponse(event, messageType);
    wsMessageResponse.content = message;
    ws.send(JSON.stringify(wsMessageResponse), {}, (err) => {
      if (err) {
        this.removeUser(id);
        this.logger.info('用户掉线');
        onSendErr(err);
        return;
      }
      this.logger.debug('消息成功发送');
    });
  }

  private handleMessage(data: WSMessageResponse) {
    const messageBody: WSMessageResponse = JSON.parse(data.toString());
    this.logger.debug(messageBody);
  }

  public isUserOnline(id: string): boolean {
    return this.map.has(id);
  }

  public removeUser(id: string) {
    this.map.delete(id);
  }
}
