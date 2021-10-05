import { User } from './user';
import { client } from './client';

export class UsersService {
  public async getList(): Promise<User[]> {
    return client<User[]>('/api/users/users');
  }
}

export const usersService = new UsersService();
