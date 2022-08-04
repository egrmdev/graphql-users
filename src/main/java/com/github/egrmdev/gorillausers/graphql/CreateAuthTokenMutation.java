
package com.github.egrmdev.gorillausers.graphql;

import com.github.egrmdev.gorillausers.service.TokenService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CreateAuthTokenMutation implements GraphQLMutationResolver {

    private final TokenService userTokenService;

    @Autowired
    public CreateAuthTokenMutation(TokenService userTokenService) {
        this.userTokenService = userTokenService;
    }

    public Mono<String> createAuthToken(String userName, String passwordHash) {
        return userTokenService.getTokenForUser(userName, passwordHash);
    }
}

