
package com.github.egrmdev.gorillausers.service;

import com.github.egrmdev.gorillausers.model.User;
import com.github.egrmdev.gorillausers.model.UserToken;
import com.github.egrmdev.gorillausers.repository.TokenRepository;
import com.github.egrmdev.gorillausers.util.TokenEncoder;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TokenService {
    private final UserService userService;

    private final TokenRepository tokenRepository;
    @Autowired
    public TokenService(UserService userService, TokenRepository tokenRepository) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    public Mono<String> getTokenForUser(String userName, String passwordHash) {
        return userService.getUserByName(userName)
                .filter(user -> user.getHashedPassword().equalsIgnoreCase(passwordHash))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid userName or passwordHash")))
                .flatMap(this::getOldIfExistingAndValidOrCreateNewOne)
                .map(TokenEncoder::createUserTokenForApi);
    }

    @VisibleForTesting
    Mono<UserToken> getOldIfExistingAndValidOrCreateNewOne(User user) {
        return tokenRepository.getToken(user.getUserId())
                .filter(UserToken::isNotExpired)
                .switchIfEmpty(createAndSaveNewToken(user));
    }

    private Mono<UserToken> createAndSaveNewToken(User user) {
        return Mono.just(UserToken.from(user))
                .doOnNext(userToken -> tokenRepository.addToken(user.getUserId(), userToken));
    }

}

