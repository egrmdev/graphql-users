
package com.github.egrmdev.gorillausers.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.egrmdev.gorillausers.model.User;
import com.github.egrmdev.gorillausers.model.UserInput;
import com.github.egrmdev.gorillausers.util.EncoderDecoderUtil;
import com.github.egrmdev.gorillausers.util.UsersReader;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateAuthTokenMutationTestIT {
    @LocalServerPort
    private int randomServerPort;

    private GraphQLWebClient graphqlClient;

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .build();

    @BeforeEach
    void beforeEach() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + randomServerPort + "/graphql")
                .build();
        graphqlClient = GraphQLWebClient.newInstance(webClient, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Creates user auth token")
    void createAuthToken() {
        Path path = Paths.get("src", "test", "resources", "data", "users.json");
        UsersReader usersReader = new UsersReader(new FileSystemResource(path));
        User user = usersReader.getUsers().stream().limit(1).findAny().map(User::from).get();
        String token = graphqlClient.post("mutation-create-auth-token.graphql",
                Map.of("userName", user.getUserName(), "passwordHash", user.getHashedPassword()),
                String.class).block();
        assertThat(token).isNotNull();
        assertThatNoException().isThrownBy(() -> EncoderDecoderUtil.base64Decode(token));
    }
}
