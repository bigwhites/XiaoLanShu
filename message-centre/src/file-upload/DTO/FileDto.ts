export class FileDto {
  constructor(fileName: string, filePath: string) {
    this.fileName = fileName;
    this.filePath = filePath;
  }
  public fileName: string;
  public filePath: string;
}
