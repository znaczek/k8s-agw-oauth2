export const storeSession = (sessionId: string | null, sessionExpiresIn: string | null) => {
  if (sessionId) {
    window.localStorage.setItem("SESSION", sessionId);
  }
  if (sessionExpiresIn) {
    window.localStorage.setItem("SESSION_EXPIRES_IN", sessionExpiresIn);
  }
}

export const getSession = () => {
  return {
    session: window.localStorage.getItem("SESSION"),
    sessionExpiresIn: window.localStorage.getItem("SESSION_EXPIRES_IN"),
  }
}
