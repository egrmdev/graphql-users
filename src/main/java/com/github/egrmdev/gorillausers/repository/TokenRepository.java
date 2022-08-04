
package com.github.egrmdev.gorillausers.repository;

import com.github.egrmdev.gorillausers.model.UserToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenRepository {
    private final Map<UUID, UserToken> userToToken = new ConcurrentHashMap<>();

    public Mono<UserToken> getToken(UUID userId) {
        return Mono.justOrEmpty(userToToken.get(userId));
    }

    public void addToken(UUID userId, UserToken userToken) {
        userToToken.put(userId, userToken);
    }

}

