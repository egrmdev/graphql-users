
package com.github.egrmdev.gorillausers.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.egrmdev.gorillausers.model.UserToken;
import lombok.SneakyThrows;

public class TokenEncoder {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    static {
        JSON_MAPPER.registerModule(new ParameterNamesModule());
        JSON_MAPPER.registerModule(new JavaTimeModule());
        JSON_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        JSON_MAPPER.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    }

    private TokenEncoder() {
    }

    @SneakyThrows
    public static String createUserTokenForApi(UserToken userToken) {
        return EncoderDecoderUtil.hashAndBase64Encode(JSON_MAPPER.writeValueAsString(userToken));
    }
}

