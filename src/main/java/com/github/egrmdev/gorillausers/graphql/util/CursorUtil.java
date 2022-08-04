
package com.github.egrmdev.gorillausers.graphql.util;

import com.github.egrmdev.gorillausers.graphql.model.UsersCursor;
import com.github.egrmdev.gorillausers.graphql.model.UserEdge;
import com.github.egrmdev.gorillausers.util.EncoderDecoderUtil;

import java.util.List;

public class CursorUtil {
    private CursorUtil() {}

    public static UsersCursor from(String userId) {
        return new UsersCursor(EncoderDecoderUtil.base64Encode(userId));
    }

    public static UsersCursor getFirst(List<UserEdge> edges) {
        return edges.isEmpty() ? null : edges.get(0).getCursor();
    }

    public static UsersCursor getLast(List<UserEdge> edges) {
        return edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor();
    }
}

