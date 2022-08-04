
package com.github.egrmdev.gorillausers.service;

import com.github.egrmdev.gorillausers.model.User;
import com.github.egrmdev.gorillausers.model.UserInput;
import com.github.egrmdev.gorillausers.util.UsersReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final List<User> users;

    @Autowired
    public UserService(UsersReader usersReader) {
        users = usersReader.getUsers().stream()
                .map(User::from)
                // needs to be sorted for the cursoring to work, cursoring won't work correctly if new users can be added to `users`
                .sorted(Comparator.comparing(User::getUserId))
                .collect(Collectors.toUnmodifiableList());
    }

    public Mono<User> getUserByName(String userName) {
        return users.stream()
                .filter(user -> user.getUserName().equals(userName))
                .findFirst()
                .map(Mono::just)
                .orElse(Mono.empty());
    }

    public Flux<User> getAllUsers(int first) {
        return Flux.fromStream(users.stream()
                .limit(first)
        );
    }

    public Flux<User> getUsersAfter(int first, UUID id) {
        return Flux.fromStream(users.stream()
                .dropWhile(user -> user.getUserId().compareTo(id) <= 0)
                .limit(first)
        );
    }

    public List<User> getUsers() {
        return users;
    }
}

