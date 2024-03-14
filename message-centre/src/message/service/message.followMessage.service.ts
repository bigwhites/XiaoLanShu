import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { FollowMessage } from '../../Entity/FollowMessage';

@Injectable()
export class FollowMessageService {
  constructor(
    @InjectRepository(FollowMessage)
    private userRepository: Repository<FollowMessage>,
  ) {}

  async findAll(): Promise<FollowMessage[]> {
    return this.userRepository.find();
  }

  async create(data: Partial<FollowMessage>): Promise<FollowMessage> {
    const user = this.userRepository.create(data);
    return this.userRepository.save(user);
  }
}
