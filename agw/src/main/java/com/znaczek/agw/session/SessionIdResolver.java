package com.znaczek.agw.session;

import com.znaczek.agw.security.LoginLinksProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.util.List;

/**
 * Session id resolver delegates to an appropriate session resolver.
 * Normally we want to use headers, but for OAuth2 authentication dance session needs to be passed in a cookie.
 * which in this case are two situations: authorization request and code exchange.
 *
 * For logout we don't need cookie, because after logout the session is gone anyway, so we don't need te retrieve it.
 * We only need to pass session when reaching logout, but later it's not needed. That's why we pass session in query param.
 */
public class SessionIdResolver implements WebSessionIdResolver {

  private final LoginLinksProvider loginLinksProvider;
  private final CookieWebSessionIdResolver cookieWebSessionIdResolver = new CookieWebSessionIdResolver();
  private final HeaderWebSessionIdResolver headerWebSessionIdResolver = new HeaderWebSessionIdResolver();

  public SessionIdResolver(LoginLinksProvider loginLinksProvider) {
    this.loginLinksProvider = loginLinksProvider;
    headerWebSessionIdResolver.setHeaderName("X-SESSION");
  }

  @Override
  public List<String> resolveSessionIds(@NotNull ServerWebExchange exchange) {
    if (isCodeExchangeRequest(exchange)) {
      return cookieWebSessionIdResolver.resolveSessionIds(exchange);
    } else if (isLogoutRequest(exchange)) {
      return exchange.getRequest().getQueryParams().get("session");
    }
    return headerWebSessionIdResolver.resolveSessionIds(exchange);
  }

  @Override
  public void setSessionId(@NotNull ServerWebExchange exchange, @NotNull String id) {
    if (isAuthorizationRequest(exchange)) {
      cookieWebSessionIdResolver.setSessionId(exchange, id);
    } else {
      headerWebSessionIdResolver.setSessionId(exchange, id);
    }
  }

  @Override
  public void expireSession(@NotNull ServerWebExchange exchange) {
    cookieWebSessionIdResolver.expireSession(exchange);
    headerWebSessionIdResolver.expireSession(exchange);
  }

  public void clearCookieSessionId(@NotNull ServerWebExchange exchange) {
    cookieWebSessionIdResolver.expireSession(exchange);
  }

  /**
   * URI prefix is the default spring security one taken from
   * {@link org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver#DEFAULT_AUTHORIZATION_REQUEST_PATTERN}.
   */
  private boolean isAuthorizationRequest(ServerWebExchange exchange) {
    return exchange.getRequest().getURI().getPath().startsWith(loginLinksProvider.provide());
  }

  /**
   * This is the default sprinig security Oauth2 code exchange matcher taken from {@link org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2LoginSpec#createAttemptAuthenticationRequestMatcher}
   */
  private boolean isCodeExchangeRequest(ServerWebExchange exchange) {
    return exchange.getRequest().getURI().getPath().startsWith("/login/oauth2/code");
  }


  /**
   * Taken from {@link org.springframework.security.web.server.authentication.logout.LogoutWebFilter#requiresLogout}
   */
  private boolean isLogoutRequest(ServerWebExchange exchange) {
    return HttpMethod.POST.equals(exchange.getRequest().getMethod()) &&
      exchange.getRequest().getURI().getPath().equals("/logout");
  }

}
