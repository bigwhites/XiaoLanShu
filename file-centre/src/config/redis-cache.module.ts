import { Module } from '@nestjs/common';
import { RedisModule } from '@nestjs-modules/ioredis';

@Module({
  imports: [
    RedisModule.forRootAsync({
      useFactory: () => ({
        type: 'single',
        url: 'redis://localhost:6379',
        options: {
          host: 'localhost',
          port: 6379,
        },
        // db: 3,
      }),
    }),
  ],
})
export class RedisCacheModule {}
