package com.znaczek.agw.session;

import com.znaczek.agw.security.LoginLinksProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.session.WebSessionIdResolver;

@Configuration
@RequiredArgsConstructor
public class SessionConfig {

  private final LoginLinksProvider loginLinksProvider;

  @Bean
  public WebSessionIdResolver getSessionIdResolver() {
    return new SessionIdResolver(loginLinksProvider);
  }

}
