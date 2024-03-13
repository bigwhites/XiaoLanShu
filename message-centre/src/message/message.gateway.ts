import {
  WebSocketServer,
  OnGatewayConnection,
  OnGatewayDisconnect,
  WebSocketGateway,
} from '@nestjs/websockets';
import { Server, WebSocket } from 'ws';
import { Injectable } from '@nestjs/common';

@Injectable()
@WebSocketGateway(3002)
export class WSMessageGateway
  implements OnGatewayConnection, OnGatewayDisconnect
{
  @WebSocketServer()
  server: Server;
  map: Map<string, WebSocket>;

  constructor() {
    this.map = new Map();
  }

  async handleConnection(ws: WebSocket) {
    console.log('Client connected');
    console.log(ws);

    ws.on('message', (data) => {
      ws.send('Hello from server');
      console.log(data.toString());
      const id = JSON.parse(data.toString()).data.id;
      console.log(id);
      this.map.set(id, ws);
      console.log(data.toString());
      ws.send('Hello from server');
    });
  }

  async handleDisconnect() {
    console.log('Client disconnected');
  }

  sendMessage(message: string) {
    const newVar: WebSocket = this.map.get('1');
    newVar.send(message);
  }
}
