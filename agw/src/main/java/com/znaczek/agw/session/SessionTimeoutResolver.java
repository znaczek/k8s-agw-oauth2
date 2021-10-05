package com.znaczek.agw.session;

import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

import java.time.Instant;

@Component
public class SessionTimeoutResolver {

  public static final String SESSION_EXPIRATION = "X-SESSION-EXPIRES-IN";

  public String getTimeout(WebSession session) {
    Instant sessionExpiresIn =  session.getLastAccessTime().plus(session.getMaxIdleTime());
    return String.valueOf(sessionExpiresIn.getEpochSecond());
  }
}
