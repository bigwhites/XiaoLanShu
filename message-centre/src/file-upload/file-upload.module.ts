import {
  Module,
  OnApplicationBootstrap,
  OnModuleDestroy,
} from '@nestjs/common';
import { FileUploadService } from './file-upload.service';
import { FileUploadController } from './file-upload.controller';

@Module({
  providers: [FileUploadService],
  controllers: [FileUploadController],
})
export class FileUploadModule
  implements OnApplicationBootstrap, OnModuleDestroy
{
  constructor(private readonly service: FileUploadService) {}

  async onApplicationBootstrap() {
    await this.service.connectToQueue();
  }

  async onModuleDestroy() {
    this.service.closeConnection();
  }
}
