
package com.github.egrmdev.gorillausers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor = @__({@JsonCreator}))
@ToString
@EqualsAndHashCode
public class UserInput {
    @NotBlank
    String firstName;
    @NotBlank
    String lastName;
    @NotBlank
    String userName;
    @NotBlank
    String password;
    @Email
    @NotBlank
    String email;
}

