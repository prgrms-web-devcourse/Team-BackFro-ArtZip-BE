package com.prgrms.artzip.common.jwt;

import org.springframework.util.Assert;

public class JwtPrincipal {

  public final String accessToken;

  public final String email;

  public final Long userId;

  JwtPrincipal(String accessToken, String email, Long userId) {
    Assert.hasText(accessToken, "accessToken이 공백이거나 누락되었습니다.");
    Assert.hasText(email, "email 공백이거나 누락되었습니다.");
    Assert.notNull(userId,"userId가 누락되었습니다.");

    this.accessToken = accessToken;
    this.email = email;
    this.userId = userId;
  }
}
