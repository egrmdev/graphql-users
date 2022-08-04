
package com.github.egrmdev.gorillausers.graphql.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static graphql.Assert.assertNotNull;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class UserDTOConnection {
    List<UserEdge> edges;
    UserPageInfo pageInfo;

    @JsonCreator
    public UserDTOConnection(List<UserEdge> edges, UserPageInfo pageInfo) {
        this.edges = List.copyOf(assertNotNull(edges, () -> "edges cannot be null"));
        this.pageInfo = assertNotNull(pageInfo, () -> "page info cannot be null");
    }
}

