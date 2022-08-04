
package com.github.egrmdev.gorillausers.graphql.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.egrmdev.gorillausers.model.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor = @__({@JsonCreator}))
@ToString
@EqualsAndHashCode
public class UserDTO {
    String id;
    String firstName;
    String lastName;
    String userName;
    String email;

    public static UserDTO from(User user) {
        return new UserDTO(user.getUserId().toString(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail());
    }
}

