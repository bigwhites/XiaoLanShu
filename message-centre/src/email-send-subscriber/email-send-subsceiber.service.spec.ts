import { Test, TestingModule } from '@nestjs/testing';
import { EmailSendSubscriberService } from './email-send-subscriber.service';

describe('EmailSendSubsceiberService', () => {
  let service: EmailSendSubscriberService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [EmailSendSubscriberService],
    }).compile();

    service = module.get<EmailSendSubscriberService>(EmailSendSubscriberService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
