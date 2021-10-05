package com.znaczek.agw.filters;


import com.znaczek.agw.security.EmptyAuthentication;
import com.znaczek.agw.session.SessionTimeoutResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionFilter implements GlobalFilter {

  private final SessionTimeoutResolver sessionTimeoutResolver;
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    return chain.filter(exchange)
      .then(exchange.getSession())
      .zipWith(
        exchange.getPrincipal()
          .cast(Authentication.class)
          .defaultIfEmpty(new EmptyAuthentication())
      )
      .doOnNext(z -> {
        Authentication a = z.getT2();
        if (a.isAuthenticated()) {
          WebSession s = z.getT1();
          exchange.getResponse().getHeaders().add(SessionTimeoutResolver.SESSION_EXPIRATION, sessionTimeoutResolver.getTimeout(s));
        }
      }).then();
  }

}
