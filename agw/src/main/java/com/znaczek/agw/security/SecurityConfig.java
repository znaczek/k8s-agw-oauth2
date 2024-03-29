package com.znaczek.agw.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  public final static String AUTH_ENTRYPOINT_HEADER_NAME = "X-auth-entrypoint";

  private final AuthorizationRequestResolver authorizationRequestResolver;
  private final LogoutSuccessHandler logoutSuccessHandler;
  private final AuthenticationSuccessHandler authenticationSuccessHandler;
  private final AuthenticationFailureHandler authenticationFailureHandler;

  /**
   * Most of the spring security classes that store data are already implemented using WebSession.
   * The only one that is not is {@link ServerOAuth2AuthorizedClientRepository} so we define that bean ourselves.
   * @return ServerOAuth2AuthorizedClientRepository
   */
  @Bean
  public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
    return new WebSessionServerOAuth2AuthorizedClientRepository();
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    return http
      .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
      .authorizeExchange(ae -> ae
        .anyExchange()
        .permitAll()
      )
      .oauth2Login(l -> l
        .authorizedClientRepository(authorizedClientRepository())
        .authorizationRequestResolver(authorizationRequestResolver)
        .authenticationSuccessHandler(authenticationSuccessHandler)
        .authenticationFailureHandler(authenticationFailureHandler)
      )
      .logout(l -> l
        .logoutSuccessHandler(logoutSuccessHandler)
      )
      .csrf()
      .disable()
      .build();
  }

}
