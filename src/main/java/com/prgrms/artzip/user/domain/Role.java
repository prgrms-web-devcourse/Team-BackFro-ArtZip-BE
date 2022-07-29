package com.prgrms.artzip.user.domain;

import static com.prgrms.artzip.common.ErrorCode.MISSING_REQUEST_PARAMETER;
import static java.util.Objects.*;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    private static final int MAX_AUTHORITY = 10;

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false, length = 10)
    private Authority authority;

    public Role(Authority authority) {
        if(isNull(authority)) throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
        this.authority = authority;
    }

}
