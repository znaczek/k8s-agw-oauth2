package com.znaczek.agw.security;

import com.znaczek.agw.session.SessionIdResolver;
import com.znaczek.agw.session.SessionTimeoutResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.WebSessionIdResolver;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * After successful authentication we redirect user to "/" with `login=true` query parameter.
 * Client app can consume this parameter and perform some action after the user has logged in.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

  private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();
  private final SessionTimeoutResolver sessionTimeoutResolver;
  private final WebSessionIdResolver sessionIdResolver;

  /**
   * According to {@link com.znaczek.agw.session.SessionIdResolver} during authentication dance we leverage on a cookie.
   * On authentication success we want to switch back to headers,
   * that's why we expire the session cookie and send the session in query params
   * to allow the client to get the session metadata.
   */
  @Override
  public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
    ServerWebExchange exchange = webFilterExchange.getExchange();
    ((SessionIdResolver)sessionIdResolver).clearCookieSessionId(exchange);
    return exchange.getSession()
      .flatMap(s -> this.redirectStrategy.sendRedirect(exchange, URI.create(
        String.format("/?login=true&session=%s&session_expires_in=%s", s.getId(), sessionTimeoutResolver.getTimeout(s)))
      ));
  }
}
