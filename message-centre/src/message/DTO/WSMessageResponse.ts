export class WSMessageResponse {
  public event: string;

  public content: any;

  public messageType: number;

  public sentTime: Date;

  constructor(event: string, messageType: number) {
    this.event = event;
    this.messageType = messageType;
    this.sentTime = new Date();
  }
}

export enum MessageType {
  AGREE = 0,
  COLLECTION = 1,
  FOLLOW = 2,
}

export enum MessageEvent {
  AGREE = 'agree',
  COLLECTION = 'collection',
  FOLLOW = 'follow',
}
