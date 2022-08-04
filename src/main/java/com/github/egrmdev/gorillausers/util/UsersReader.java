
package com.github.egrmdev.gorillausers.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.egrmdev.gorillausers.model.UserInput;
import com.google.common.annotations.VisibleForTesting;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsersReader {
    private final Set<UserInput> users;

    public UsersReader(@Value("classpath:data/users.json") Resource resourceFile) {
        List<UserInput> userInputs = readUsersFromFile(resourceFile);
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final Set<ConstraintViolation<UserInput>> violations = userInputs.stream()
                .map(user -> validator.validate(user))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).collect(Collectors.joining("; "))
            );
        }
        throwIfUserNameIsNotUnique(userInputs);
        users = new HashSet<>(userInputs);
    }

    @NotNull
    @SneakyThrows
    private List<UserInput> readUsersFromFile(Resource resourceFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());
        return Collections.unmodifiableList(objectMapper.readValue(resourceFile.getFile(), new TypeReference<>() {
        }));
    }

    @VisibleForTesting
    static void throwIfUserNameIsNotUnique(List<UserInput> userInputs) {
        Map<String, Set<UserInput>> userNamesToUsers = userInputs.stream()
                .collect(Collectors.groupingBy(UserInput::getUserName, Collectors.toSet()));
        final Set<String> usersWithNonUniqueNames = userNamesToUsers.values().stream()
                .filter(userInputsPerUserName -> userInputsPerUserName.size() > 1)
                .flatMap(Set::stream)
                .map(UserInput::getUserName)
                .collect(Collectors.toSet());
        if (!usersWithNonUniqueNames.isEmpty()) {
            throw new IllegalArgumentException("There are multiple users having the same username(s): "
                    + String.join(", ", usersWithNonUniqueNames));
        }
    }

    public Set<UserInput> getUsers() {
        return users;
    }
}

