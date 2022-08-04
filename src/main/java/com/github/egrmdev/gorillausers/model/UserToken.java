
package com.github.egrmdev.gorillausers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor = @__({@JsonCreator}))
@ToString
@EqualsAndHashCode
public class UserToken {
    UUID userId;
    String passwordHash;
    Instant expirationTime;

    @JsonIgnore
    public boolean isNotExpired() {
        return expirationTime.isAfter(Instant.now());
    }

    public static UserToken from(User user) {
        return new UserToken(user.getUserId(), user.getHashedPassword(), Instant.now().plus(60, ChronoUnit.MINUTES));
    }
}

