import { NestApplication, NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import * as AppConfig from './config/AppConfig';
import { WsAdapter } from '@nestjs/platform-ws';

async function bootstrap() {
  const app = await NestFactory.create<NestApplication>(AppModule);
  app.useWebSocketAdapter(new WsAdapter(app));
  app.useStaticAssets(AppConfig.FILE_ROOT, {
    // 配置静态资源目录
    prefix: AppConfig.STATIC_ROUTE,
  });
  app.enableCors();
  await app.listen(3000);
}

bootstrap();
