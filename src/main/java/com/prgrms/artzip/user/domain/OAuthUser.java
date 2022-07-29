package com.prgrms.artzip.user.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("OAUTH")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthUser extends User{

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Builder
    public OAuthUser(String email, String nickname, String provider, String providerId, List<Role> roles) {
        super(email, nickname, roles);
        this.provider = provider;
        this.providerId = providerId;
    }
}
