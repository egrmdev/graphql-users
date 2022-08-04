
package com.github.egrmdev.gorillausers.graphql.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import graphql.relay.PageInfo;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class UserPageInfo implements PageInfo {
    UsersCursor startCursor;
    UsersCursor endCursor;
    boolean hasPreviousPage;
    boolean hasNextPage;

    @JsonCreator
    public UserPageInfo(UsersCursor startCursor, UsersCursor endCursor, boolean hasPreviousPage, boolean hasNextPage) {
        this.startCursor = startCursor;
        this.endCursor = endCursor;
        this.hasPreviousPage = hasPreviousPage;
        this.hasNextPage = hasNextPage;
    }
}

