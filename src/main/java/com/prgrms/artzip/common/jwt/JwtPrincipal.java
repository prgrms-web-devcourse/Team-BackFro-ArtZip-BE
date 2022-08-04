package com.prgrms.artzip.common.jwt;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.user.domain.User;
import lombok.Getter;

import static com.prgrms.artzip.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;
import static java.util.Objects.*;

@Getter
public class JwtPrincipal {

  private final String accessToken;

  private final User user;

  JwtPrincipal(String accessToken, User user) {
    if(!hasText(accessToken)) throw new InvalidRequestException(EMAIL_REQUIRED);
    if(isNull(user)) throw new InvalidRequestException(USER_PARAM_REQUIRED);

    this.accessToken = accessToken;
    this.user = user;
  }
}
