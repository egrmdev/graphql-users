
package com.github.egrmdev.gorillausers.graphql.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import graphql.relay.Edge;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import static graphql.Assert.assertNotNull;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class UserEdge implements Edge<UserDTO> {
    UserDTO node;
    UsersCursor cursor;

    @JsonCreator
    public UserEdge(UserDTO node, UsersCursor cursor) {
        this.cursor = assertNotNull(cursor, () -> "cursor cannot be null");
        this.node = node;
    }

}
