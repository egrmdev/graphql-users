
package com.github.egrmdev.gorillausers.graphql;

import com.github.egrmdev.gorillausers.graphql.model.UserDTO;
import com.github.egrmdev.gorillausers.graphql.model.UserDTOConnection;
import com.github.egrmdev.gorillausers.graphql.model.UserEdge;
import com.github.egrmdev.gorillausers.graphql.model.UserPageInfo;
import com.github.egrmdev.gorillausers.graphql.util.CursorUtil;
import com.github.egrmdev.gorillausers.model.User;
import com.github.egrmdev.gorillausers.service.UserService;
import com.github.egrmdev.gorillausers.util.EncoderDecoderUtil;
import com.google.common.annotations.VisibleForTesting;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Component
public class UsersQuery implements GraphQLQueryResolver {

    private final UserService userService;

    @Autowired
    public UsersQuery(UserService userService) {
        this.userService = userService;
    }

    public Mono<UserDTOConnection> users(int first, @Nullable String cursor) {
        return getUsers(first, cursor)
                .map(UserDTO::from)
                .map(this::getUserDTODefaultEdge)
                .collectList()
                .map(edges -> new UserDTOConnection(edges, getPageInfo(first, cursor, edges)));
    }

    @NotNull
    private UserPageInfo getPageInfo(int first, @Nullable String cursor, List<UserEdge> edges) {
        return new UserPageInfo(CursorUtil.getFirst(edges), CursorUtil.getLast(edges), cursor != null, edges.size() >= first);
    }

    @NotNull
    private UserEdge getUserDTODefaultEdge(UserDTO user) {
        return new UserEdge(user, CursorUtil.from(user.getId()));
    }

    @VisibleForTesting
    Flux<User> getUsers(int first, @Nullable String cursor) {
        if (cursor != null) {
            try {
                UUID userId = UUID.fromString(EncoderDecoderUtil.base64Decode(cursor));
                return userService.getUsersAfter(first, userId);
            } catch (IllegalArgumentException ignored) {
                return Flux.error(new IllegalArgumentException("Invalid cursor value"));
            }
        } else {
            return userService.getAllUsers(first);
        }
    }
}

