import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity('follow_message')
export class FollowMessage {
  @PrimaryGeneratedColumn()
  public id: number;

  @Column({ name: 'from_uuid' })
  public fromUuid: string;
  @Column({ name: 'to_uuid' })
  public toUuid: string;
  @Column({ name: 'message_time' })
  public messageTime: Date;
  @Column({ name: 'status' })
  public status: string;
  @Column({ name: 'avatar' })
  public avatar: string;
}
