import { getSession, storeSession } from '../auth/session-manager';

const getCommonHeaders = () => {
  const headers: {[key: string]: string} = {'content-type': 'application/json'};
  const sessionId = getSession().session;
  if (sessionId) {
    headers['X-SESSION'] = sessionId;
  }
  return headers;
}

export function client<T>(endpoint: string, options?: RequestInit & {raw: false}): Promise<T>;

export function client<T>(endpoint: string, options?: RequestInit & {raw: true}): Promise<Response>;

export function client<T>(endpoint: string, options?: RequestInit & {raw: boolean}): any {
  const commonHeaders = getCommonHeaders()

  const config = {
    method: options?.body ? 'POST' : 'GET',
    headers: {
      ...commonHeaders,
      ...(options?.headers || []),
    },
    ...options,
  }
  if (options?.body) {
    config.body = JSON.stringify(options.body)
  }

  return window
    .fetch(endpoint, config)
    .then(async response => {
      storeSession(response.headers.get('X-SESSION'), response.headers.get('X-SESSION-EXPIRES-IN'));
      if (options?.raw) {
        return response.ok ? response : Promise.reject(response)
      } else {
        if (response.ok) {
          return await response.json()
        } else {
          const errorMessage = await response.text()
          return Promise.reject(new Error(errorMessage))
        }
      }
    })
}
