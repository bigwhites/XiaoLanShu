import { WebSocketServer, WebSocketGateway } from '@nestjs/websockets';
import { Server, WebSocket } from 'ws';
import { Inject, Injectable, OnModuleInit } from '@nestjs/common';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';
import { WSMessageResponse } from './DTO/WSMessageResponse';

@Injectable()
@WebSocketGateway(3002)
export class WSMessageGateway implements OnModuleInit {
  @WebSocketServer()
  server: Server;
  map: Map<string, WebSocket>;

  constructor(
    @Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger,
  ) {
    this.map = new Map();
  }

  onModuleInit() {
    this.server.on('connection', (ws, request) => {
      this.logger.info(request.url);
      ws.send('ok');
      this.map.set(request.url.substr(1), ws);

      ws.on('message', (data) => {
        const messageBody: WSMessageResponse = JSON.parse(data.toString());
        console.log(messageBody);
        ws.send('sdada');
      });
    });
  }

  async sendMessage(message: string, id: string) {
    const ws: WebSocket = this.map.get('sss');
    ws.send(message);
  }
}
