package com.prgrms.artzip.common.jwt.claims;

import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;

import java.util.Date;

@Getter
public class AccessClaim implements Claims {
  private Long userId;

  private String email;
  private String[] roles;
  private Date iat; // 발행 시각
  private Date exp; // 만료 시각

  public AccessClaim(Long userId, String email, String[] roles){
    this.userId = userId;
    this.email = email;
    this.roles = roles;
  };

  public AccessClaim(DecodedJWT decodedJWT) {
    Claim userId = decodedJWT.getClaim("userId");
    if (!userId.isNull()) {
      this.userId = userId.asLong();
    }
    Claim email = decodedJWT.getClaim("email");
    if (!email.isNull()) {
      this.email = email.asString();
    }
    Claim roles = decodedJWT.getClaim("roles");
    if (!roles.isNull()) {
      this.roles = roles.asArray(String.class);
    }
    this.iat = decodedJWT.getIssuedAt();
    this.exp = decodedJWT.getExpiresAt();
  }

  @Override
  public void applyToBuilder(Builder builder) {
    builder.withClaim("userId", this.userId);
    builder.withClaim("email", this.email);
    builder.withArrayClaim("roles", this.roles);
  }
}
