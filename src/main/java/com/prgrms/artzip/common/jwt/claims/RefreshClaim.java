package com.prgrms.artzip.common.jwt.claims;

import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.prgrms.artzip.common.jwt.claims.Claims;
import lombok.Getter;

import java.util.Date;

@Getter
public class RefreshClaim implements Claims {

  private String email ;
  private Date iat; // 발행 시각
  private Date exp; // 만료 시각

  public RefreshClaim(String email){
    this.email = email;
  };

  public RefreshClaim(DecodedJWT decodedJWT) {
    Claim email = decodedJWT.getClaim("email");
    if (!email.isNull()) {
      this.email = email.asString();
    }
    this.iat = decodedJWT.getIssuedAt();
    this.exp = decodedJWT.getExpiresAt();
  }

  @Override
  public void applyToBuilder(Builder builder) {
    builder.withClaim("email", this.email);
  }
}
