package com.prgrms.artzip.common.oauth;

import com.prgrms.artzip.user.domain.OAuthUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@RequiredArgsConstructor
public class OAuthUserPrincipal implements OAuth2User {

  private final OAuthUser oAuthUser;
  private final Map<String, Object> attributes;

  @Override
  public <A> A getAttribute(String name) {
    return OAuth2User.super.getAttribute(name);
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oAuthUser.getRoles().stream().map(Role::toGrantedAuthority).toList();
  }

  @Override
  public String getName() {
    return oAuthUser.getProviderId();
  }
}
