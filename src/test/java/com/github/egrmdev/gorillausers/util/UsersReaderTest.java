
package com.github.egrmdev.gorillausers.util;

import com.github.egrmdev.gorillausers.model.UserInput;
import com.github.egrmdev.gorillausers.util.UsersReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsersReaderTest {

    @Test
    @DisplayName("Loads users from file correctly")
    void loadUsers() {
        Path path = Paths.get("src", "test", "resources", "data", "users.json");
        assertThat(path.toAbsolutePath().toString()).endsWith("/src/test/resources/data/users.json");
        UsersReader usersReader = new UsersReader(new FileSystemResource(path));
        assertThat(usersReader.getUsers()).containsExactlyInAnyOrder(
                new UserInput("Parker", "Lubowitz", "Patton Down DeHatches", "DmFPPuM9", "lanette.cronin.dvm@yahoo.com"),
                new UserInput("Gray", "Buckridge", "Misty Shore", "ztThfXcB", "granville.rempel.lld@hotmail.com")
        );
    }

    @Test
    @DisplayName("Throws if users don't have all the properties set")
    void loadUsersThrowsIfValidationFails() {
        Path path = Paths.get("src", "test", "resources", "data", "bad-users.json");
        assertThat(path.toAbsolutePath().toString()).endsWith("/src/test/resources/data/bad-users.json");
        assertThatThrownBy(() -> new UsersReader(new FileSystemResource(path)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContainingAll("lastName: must not be blank",
                        "firstName: must not be blank",
                        "email: must not be blank",
                        "userName: must not be blank",
                        "password: must not be blank");
    }

    @Test
    @DisplayName("Throws if there are multiple users with the same user name")
    void loadUsersThrowsIfUserNameNotUnique() {
        List<UserInput> userInputs = List.of(
                new UserInput("Alice", "Ada", "a_ada", "secret", "alice@example.com"),
                new UserInput("Bob", "Bane", "a_ada", "geheim", "bob@example.com")
        );
        assertThatThrownBy(() -> UsersReader.throwIfUserNameIsNotUnique(userInputs))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("There are multiple users having the same username(s)");
    }
}
