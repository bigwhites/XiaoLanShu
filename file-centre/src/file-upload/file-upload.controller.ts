import {
  Controller,
  Post,
  UploadedFile,
  UseInterceptors,
  HttpCode,
  Inject,
  UploadedFiles,
  Req,
} from '@nestjs/common';
import { R } from '../utils/R';
import { FileInterceptor, FilesInterceptor } from '@nestjs/platform-express';
import { diskStorage } from 'multer';
import * as AppConfig from '../config/AppConfig';
import { Redis } from 'ioredis';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';
import { checkOrMkDir } from '../utils/dir-utils';
import { FileUploadService } from './file-upload.service';
import e from 'express';

@Controller('fileUpload')
export class FileUploadController {
  constructor(
    private readonly fileUploadService: FileUploadService,
    @Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger,
  ) {}

  @Post('one')
  @UseInterceptors(
    FileInterceptor('file', {
      storage: diskStorage({
        destination: async (req, file, cb) => {
          if (file.size > AppConfig.UPLOAD_MAX_IMG_SIZE) {
            cb(new Error('文件超长'), null);
          }
          const redisClient = new Redis();

          // 动态决定保存目录
          const uploadPath = AppConfig.FILE_ROOT;
          const token: string =
            req.headers[AppConfig.UPLOAD_FILE_HEADER].toString();
          const pathSuffix = await redisClient.get(token);
          redisClient.del(token);
          const fileDir = `${uploadPath}\\${pathSuffix}`;
          console.log(`upload 2 dir:${fileDir}`);
          await checkOrMkDir(fileDir);
          cb(null, fileDir);
        },
        filename: async (req, file, cb) => {
          const redisClient = new Redis();
          const fileNameKey =
            req.headers[AppConfig.FILE_NAME_HEADER].toString();
          const fileName = await redisClient.get(fileNameKey);
          redisClient.del(fileNameKey);
          cb(null, fileName);
        },
      }),
    }),
  )
  @HttpCode(200)
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  uploadOne(@UploadedFile() file: Express.Multer.File): R {
    try {
      return R.success();
    } catch (e) {
      return R.fail('失败');
    }
  }

  @Post('multiple')
  @HttpCode(200)
  @UseInterceptors(FilesInterceptor('files'))
  async uploadFile(
    @UploadedFiles() files: Array<Express.Multer.File>,
    @Req() request: Request,
  ): Promise<R> {
    try {
      await this.fileUploadService
        .uploadMultiple(files, request.headers[AppConfig.REDIS_KEY].toString())
        .catch((e) => {
          throw new Error(e);
        });
      return R.success('上传成功!');
    } catch (e: any) {
      return R.fail('失败：' + e.message);
    }
  }
}
