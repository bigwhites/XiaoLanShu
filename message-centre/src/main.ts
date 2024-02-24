import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';

async function bootstrap() {
  // const app = await NestFactory.create(AppModule);
  // // app的初始化我跳过了
  // app.connectMicroservice<MicroserviceOptions>({
  //   // 类型：rabbitmq
  //   transport: Transport.RMQ,
  //   options: {
  //     // rabbitmq地址
  //     urls: ['amqp://root:123456@localhost:5672'],
  //     // 交换机
  //     // exchange: `MailSentDirectExchange`,
  //     // routingKey: 'MailSentDirectRouting',
  //     // 队列
  //     // queue: `MailSendQueue`,
  //     queueOptions: {
  //       // 消息是否持久化
  //       durable: true,
  //       noAck: false,
  //       prefetchCount: 1,
  //     },
  //   },
  // });
  // await app.startAllMicroservices();
  // await app.listen(3000);

  // const app = await NestFactory.createMicroservice<MicroserviceOptions>(
  //   AppModule,
  //   {
  //     transport: Transport.RMQ,
  //     options: {
  //       queueOptions: {
  //         urls: ['amqp://root:123456@localhost:5672'],
  //         durable: false,
  //         noAck: true,
  //       },
  //     },
  //   },
  // );
  const app = await NestFactory.create(AppModule);
  await app.listen(3000);
}
bootstrap();