package com.prgrms.artzip.user.domain;

import com.prgrms.artzip.common.oauth.AuthProvider;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("OAUTH")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OAuthUser extends User {

  @Enumerated(EnumType.STRING)
  @Column(name = "provider")
  private AuthProvider provider;

  @Column(name = "provider_id")
  private String providerId;

  @Builder
  public OAuthUser(String email, String nickname, AuthProvider provider, String providerId,
      List<Role> roles) {
    super(email, nickname, roles);
    this.provider = provider;
    this.providerId = providerId;
  }
}
