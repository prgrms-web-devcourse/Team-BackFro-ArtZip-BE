package com.prgrms.artzip.user.domain;

import static com.prgrms.artzip.common.ErrorCode.*;

import com.prgrms.artzip.common.error.exception.AuthErrorException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@DiscriminatorValue("LOCAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalUser extends User {

    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
    private static final int MAX_PASSWORD_LENGTH = 500;

    @Column(name = "password")
    private String password;

    @Builder
    public LocalUser(String email, String nickname, String password) {
        super(email, nickname);
        this.password = password;
    }

    public void checkPassword(PasswordEncoder passwordEncoder, String credentials) {
        if (!passwordEncoder.matches(credentials, password))
            throw new AuthErrorException(INVALID_ACCOUNT_REQUEST);
    }

    private void validatePassword(String password) {
        if(password.length() > MAX_PASSWORD_LENGTH) {
            throw new InvalidRequestException(INVALID_LENGTH);
        }
        if(!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new InvalidRequestException(INVALID_INPUT_VALUE);
        }
    }

    public void changePassword(PasswordEncoder passwordEncoder, String password) {
        validatePassword(password);
        this.password = passwordEncoder.encode(password);
    }
}

