
package com.github.egrmdev.gorillausers.graphql;

import com.github.egrmdev.gorillausers.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class UsersQueryTest {
    @MockBean
    UserService userService;

    @Test
    @DisplayName("Returns error if cursor value can't be parsed")
    void throwsErrorIfCursorValueIsNotParseable() {
        UsersQuery usersQuery = new UsersQuery(userService);
        Mockito.when(userService.getAllUsers(ArgumentMatchers.anyInt())).thenReturn(Flux.empty());
        StepVerifier.create(usersQuery.getUsers(1, "garbage"))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException
                        && t.getMessage().equals("Invalid cursor value"))
                .verify();
    }
}
