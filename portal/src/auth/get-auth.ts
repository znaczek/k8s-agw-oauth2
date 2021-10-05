import { AuthData } from './auth.context';
import { client } from '../api/client';

export const getAuth = async (): Promise<AuthData> => {
  if (!SETTINGS.AUTH) {
    return {
      user: {
        name: 'hello@example.com'
      },
      loginUrl: undefined,
      error: false,
    }
  }

  let whoami;
  try {
    whoami = await client('/whoami', {raw: true});
  } catch(e) {
    const loginUrl = e.headers.get('X-auth-entrypoint');
    return {
      user: undefined,
      loginUrl,
      error: !loginUrl,
    }
  }

  const body = (await whoami.text());
  return {
    user: JSON.parse(body),
    loginUrl: undefined,
    error: false,
  }
}
