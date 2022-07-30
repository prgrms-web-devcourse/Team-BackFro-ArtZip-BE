package com.prgrms.artzip.common.jwt;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;

import static com.prgrms.artzip.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;
import static java.util.Objects.*;

public class JwtPrincipal {

  public final String accessToken;

  public final String email;

  public final Long userId;

  JwtPrincipal(String accessToken, String email, Long userId) {
    if(!hasText(accessToken)) throw new InvalidRequestException(EMAIL_REQUIRED);
    if(!hasText(email)) throw new InvalidRequestException(EMAIL_REQUIRED);
    if(isNull(userId)) throw new InvalidRequestException(EMAIL_REQUIRED);

    this.accessToken = accessToken;
    this.email = email;
    this.userId = userId;
  }
}
