
package com.github.egrmdev.gorillausers.service;

import com.github.egrmdev.gorillausers.model.User;
import com.github.egrmdev.gorillausers.model.UserInput;
import com.github.egrmdev.gorillausers.service.UserService;
import com.github.egrmdev.gorillausers.util.UsersReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @MockBean
    private UsersReader usersReader;

    @Test
    @DisplayName("Users are mapped correctly from the users provided by UsersReader")
    void userMapping() {
        UserInput userA = new UserInput("Alice", "Ada", "a_ada", "secret", "alice@example.com");
        UserInput userB = new UserInput("Bob", "Bane", "b_bane", "geheim", "bob@example.com");
        UserInput userC = new UserInput("Charlie", "Chase", "c_chase", "секрет", "charlie@example.com");

        Mockito.when(usersReader.getUsers()).thenReturn(Set.of(userA, userB, userC));
        UserService userService = new UserService(usersReader);

        assertThat(userService.getUsers())
                .usingElementComparatorIgnoringFields("userId")
                .containsExactlyInAnyOrder(User.from(userA), User.from(userB), User.from(userC))
                .isSortedAccordingTo(Comparator.comparing(User::getUserId));
    }

    @Test
    @DisplayName("User is retrieved by userName")
    void getUserByName() {
        UserInput userA = new UserInput("Alice", "Ada", "a_ada", "secret", "alice@example.com");
        UserInput userB = new UserInput("Bob", "Bane", "b_bane", "geheim", "bob@example.com");

        Mockito.when(usersReader.getUsers()).thenReturn(Set.of(userA, userB));
        UserService userService = new UserService(usersReader);

        Assertions.assertThat(userService.getUserByName("a_ada").block())
                        .usingRecursiveComparison()
                        .ignoringFields("userId")
                        .isEqualTo(User.from(userA));

        Assertions.assertThat(userService.getUserByName("b_bane").block())
                .usingRecursiveComparison()
                .ignoringFields("userId")
                .isEqualTo(User.from(userB));
    }

    @Test
    @DisplayName("First N users are retrieved")
    void getAllUsers() {
        UserInput userA = new UserInput("Alice", "Ada", "a_ada", "secret", "alice@example.com");
        UserInput userB = new UserInput("Bob", "Bane", "b_bane", "geheim", "bob@example.com");
        UserInput userC = new UserInput("Charlie", "Chase", "c_chase", "секрет", "charlie@example.com");

        Mockito.when(usersReader.getUsers()).thenReturn(Set.of(userA, userB, userC));
        UserService userService = new UserService(usersReader);

        Assertions.assertThat(userService.getAllUsers(2).collectList().block())
                .hasSize(2)
                .isSortedAccordingTo(Comparator.comparing(User::getUserId))
                .containsExactlyElementsOf(userService.getUsers().subList(0, 2));
    }

    @Test
    @DisplayName("Users are retrieved after userId")
    void getAllUsersAfter() {
        UserInput userA = new UserInput("Alice", "Ada", "a_ada", "secret", "alice@example.com");
        UserInput userB = new UserInput("Bob", "Bane", "b_bane", "geheim", "bob@example.com");
        UserInput userC = new UserInput("Charlie", "Chase", "c_chase", "секрет", "charlie@example.com");

        Mockito.when(usersReader.getUsers()).thenReturn(Set.of(userA, userB, userC));
        UserService userService = new UserService(usersReader);
        final List<User> allUsers = userService.getUsers();
        final User afterUser = allUsers.get(allUsers.size() - 2);
        Assertions.assertThat(userService.getUsersAfter(1, afterUser.getUserId()).collectList().block())
                .hasSize(1)
                .containsExactly(allUsers.get(allUsers.size() - 1));
    }
}
