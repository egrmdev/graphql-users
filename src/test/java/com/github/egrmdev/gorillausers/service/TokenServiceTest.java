
package com.github.egrmdev.gorillausers.service;

import com.github.egrmdev.gorillausers.model.User;
import com.github.egrmdev.gorillausers.model.UserToken;
import com.github.egrmdev.gorillausers.repository.TokenRepository;
import com.github.egrmdev.gorillausers.util.TokenEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class TokenServiceTest {
    @MockBean
    private UserService userService;

    @MockBean
    private TokenRepository tokenRepository;

    @Test
    @DisplayName("Returns error if user could not be found by provided userName")
    void errorsIfUserWasNotFoundByName() {
        TokenService tokenService = new TokenService(userService, tokenRepository);
        Mockito.when(userService.getUserByName(ArgumentMatchers.anyString()))
                .thenReturn(Mono.empty());
        StepVerifier.create(tokenService.getTokenForUser("foo", "bar"))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException
                        && t.getMessage().equals("Invalid userName or passwordHash"))
                .verify();
    }

    @Test
    @DisplayName("Returns error if provided password hash does not match user's hash")
    void errorsIfWrongPasswordHashProvided() {
        TokenService tokenService = new TokenService(userService, tokenRepository);
        Mockito.when(userService.getUserByName(ArgumentMatchers.anyString()))
                .thenReturn(Mono.just(
                        new User(UUID.randomUUID(), "Alice", "A", "Alice A", UUID.randomUUID().toString(), "alice@example.com"))
                );
        StepVerifier.create(tokenService.getTokenForUser("Alice A", "bar"))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException
                        && t.getMessage().equals("Invalid userName or passwordHash"))
                .verify();
    }

    @Test
    @DisplayName("Creates a new token if there is an old expired one")
    void createsNewTokenIfOldIsExpired() {
        TokenService tokenService = new TokenService(userService, tokenRepository);
        UUID userId = UUID.randomUUID();
        String passwordHash = "bar";
        UserToken oldToken = new UserToken(userId, passwordHash, Instant.now().minusSeconds(10L));

        assertThat(oldToken.isNotExpired()).isFalse();

        User u = new User(userId, "ignored", "ignored", "ignored", passwordHash, "ignored");
        Mockito.when(tokenRepository.getToken(userId)).thenReturn(Mono.just(oldToken));

        StepVerifier.create(tokenService.getOldIfExistingAndValidOrCreateNewOne(u))
                .expectNextMatches(token -> token.getUserId().equals(userId)
                        && !token.equals(oldToken)
                        && token.getPasswordHash().equals(passwordHash)
                        && token.isNotExpired())
                .verifyComplete();
        ArgumentCaptor<UserToken> argumentCaptor = ArgumentCaptor.forClass(UserToken.class);
        Mockito.verify(tokenRepository).addToken(ArgumentMatchers.eq(userId), argumentCaptor.capture());
        UserToken newToken = argumentCaptor.getValue();
        assertThat(newToken.getUserId()).isEqualTo(userId);
        assertThat(newToken.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(newToken.isNotExpired()).isTrue();
    }

    @Test
    @DisplayName("Returns an existing token if it is not expired yet")
    void returnsExistingTokenIfOldIsNotExpired() {
        TokenService tokenService = new TokenService(userService, tokenRepository);
        UUID userId = UUID.randomUUID();
        String passwordHash = "bar";
        UserToken oldToken = new UserToken(userId, passwordHash, Instant.now().plusSeconds(60));

        assertThat(oldToken.isNotExpired()).isTrue();

        User u = new User(userId, "ignored", "ignored", "ignored", passwordHash, "ignored");
        Mockito.when(tokenRepository.getToken(userId)).thenReturn(Mono.just(oldToken));

        StepVerifier.create(tokenService.getOldIfExistingAndValidOrCreateNewOne(u))
                .expectNext(oldToken)
                .verifyComplete();
        Mockito.verify(tokenRepository, Mockito.never()).addToken(ArgumentMatchers.eq(userId), ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Returns an opaque user token for the API")
    void returnsOpaqueToken() {
        TokenService tokenService = new TokenService(userService, tokenRepository);
        UUID userId = UUID.randomUUID();
        String passwordHash = "bar";
        String userName = "Alice A";
        final User user = new User(userId, "Alice", "A", userName, passwordHash, "alice@example.com");
        Mockito.when(userService.getUserByName(ArgumentMatchers.anyString())).thenReturn(Mono.just(user));
        UserToken userToken = UserToken.from(user);

        Mockito.when(tokenRepository.getToken(userId)).thenReturn(Mono.just(userToken));
        StepVerifier.create(tokenService.getTokenForUser(userName, passwordHash))
                .expectNext(TokenEncoder.createUserTokenForApi(userToken))
                .verifyComplete();
    }
}
