import * as fs from 'fs';

/**
 * 检查指定目录是否存在，不存在则创建目录。
 * @param directoryPath 目录路径
 * @returns 如果目录不存在且成功创建则返回 void
 */
export async function checkOrMkDir(directoryPath: string): Promise<void> {
  // 判断目录是否存在
  if (!fs.existsSync(directoryPath)) {
    // 如果不存在，则创建目录
    await fs.promises.mkdir(directoryPath, { recursive: true });
  }
}
