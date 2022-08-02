package com.prgrms.artzip.common.jwt.claims;


import com.auth0.jwt.JWTCreator.Builder;

public interface Claims {

  void applyToBuilder(Builder builder);

}
