
package com.github.egrmdev.gorillausers.util;

import com.google.common.hash.Hashing;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncoderDecoderUtil {

    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    private EncoderDecoderUtil() {}

    public static String hashAndBase64Encode(String input) {
        return base64Encode(sha256Hash(input));
    }

    @SneakyThrows
    public static String sha256Hash(String input) {
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }

    public static String base64Encode(byte[] input) {
        final byte[] encodedBytes = BASE64_ENCODER.encode(input);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public static String base64Encode(String input) {
        return base64Encode(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64Decode(String input) {
        return new String(BASE64_DECODER.decode(input), StandardCharsets.UTF_8);
    }
}

