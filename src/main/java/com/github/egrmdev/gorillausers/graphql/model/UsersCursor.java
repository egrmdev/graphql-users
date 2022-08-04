
package com.github.egrmdev.gorillausers.graphql.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import graphql.Assert;
import graphql.relay.ConnectionCursor;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.IOException;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@JsonDeserialize(using = UsersCursor.CursorValueDeserializer.class)
public class UsersCursor implements ConnectionCursor {
    String value;

    public UsersCursor(String value) {
        Assert.assertTrue(value != null && !value.isEmpty(), () -> "connection value cannot be null or empty");
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static class CursorValueDeserializer extends JsonDeserializer<UsersCursor> {
        @Override
        public UsersCursor deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken jsonToken = p.getCurrentToken();
            if (jsonToken == JsonToken.VALUE_STRING) {
                return new UsersCursor(p.getValueAsString());
            }
            return null;
        }
    }
}

