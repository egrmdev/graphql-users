
package com.github.egrmdev.gorillausers.model;

import com.github.egrmdev.gorillausers.util.EncoderDecoderUtil;
import lombok.Value;

import java.util.UUID;

@Value
public class User {
    UUID userId;
    String firstName;
    String lastName;
    String userName;
    String hashedPassword;
    String email;

    public static User from(UserInput userInput) {
        return new User(UUID.randomUUID(), userInput.getFirstName(), userInput.getLastName(), userInput.getUserName(),
                EncoderDecoderUtil.sha256Hash(userInput.getPassword()), userInput.getEmail());
    }
}

