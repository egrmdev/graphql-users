
package com.github.egrmdev.gorillausers.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.egrmdev.gorillausers.graphql.model.UserDTO;
import com.github.egrmdev.gorillausers.graphql.model.UserDTOConnection;
import com.github.egrmdev.gorillausers.graphql.model.UserEdge;
import com.github.egrmdev.gorillausers.graphql.model.UsersCursor;
import com.github.egrmdev.gorillausers.util.EncoderDecoderUtil;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersQueryTestIT {
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
    @DisplayName("Query all users with default first value")
    void queryAllUsersWithDefaultFirstValue() {
        UserDTO userA = new UserDTO("ignored", "Parker", "Lubowitz", "Patton Down DeHatches", "lanette.cronin.dvm@yahoo.com");
        UserDTO userB = new UserDTO("ignored", "Gray", "Buckridge", "Misty Shore", "granville.rempel.lld@hotmail.com");
        UserDTOConnection users = graphqlClient.post("query-all-users.graphql", UserDTOConnection.class).block();
        assertThat(users.getEdges()).hasSize(2)
                .allMatch(userEdge -> EncoderDecoderUtil.base64Decode(userEdge.getCursor().getValue())
                        .equals(userEdge.getNode().getId()))
                .extracting(UserEdge::getNode)
                .usingElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrderElementsOf(List.of(userA, userB));
        assertThat(users.getPageInfo().isHasNextPage()).isFalse();
        assertThat(users.getPageInfo().isHasPreviousPage()).isFalse();
        assertThat(users.getPageInfo().getStartCursor()).isEqualTo(users.getEdges().get(0).getCursor());
        assertThat(users.getPageInfo().getEndCursor()).isEqualTo(users.getEdges().get(users.getEdges().size() - 1).getCursor());
    }

    @Test
    @DisplayName("Query all users with custom first value")
    void queryAllUsersWithCustomFirstValue() {
        UserDTO userA = new UserDTO("ignored", "Parker", "Lubowitz", "Patton Down DeHatches", "lanette.cronin.dvm@yahoo.com");
        UserDTO userB = new UserDTO("ignored", "Gray", "Buckridge", "Misty Shore", "granville.rempel.lld@hotmail.com");
        UserDTOConnection users = graphqlClient.post("query-all-users-custom-first.graphql", UserDTOConnection.class).block();
        assertThat(users.getEdges()).hasSize(1)
                .allMatch(userEdge -> EncoderDecoderUtil.base64Decode(userEdge.getCursor().getValue())
                        .equals(userEdge.getNode().getId()));
        assertThat(users.getEdges().get(0).getNode())
                .satisfiesAnyOf(userDTO -> assertThat(userDTO).usingRecursiveComparison().ignoringFields("id").isEqualTo(userA),
                        userDTO -> assertThat(userDTO).usingRecursiveComparison().ignoringFields("id").isEqualTo(userB)
                );
        assertThat(users.getPageInfo().isHasNextPage()).isTrue();
        assertThat(users.getPageInfo().isHasPreviousPage()).isFalse();
        assertThat(users.getPageInfo().getStartCursor()).isEqualTo(users.getEdges().get(0).getCursor());
        assertThat(users.getPageInfo().getEndCursor()).isEqualTo(users.getEdges().get(0).getCursor());
    }

    @Test
    @DisplayName("Query all users with cursor")
    void queryUsersWithCursor() {
        UserDTOConnection allUsers = graphqlClient.post("query-all-users.graphql", UserDTOConnection.class).block();
        assertThat(allUsers.getEdges()).hasSize(2);
        UsersCursor cursor = allUsers.getEdges().get(0).getCursor();
        UserDTOConnection users = graphqlClient.post("query-all-users-with-cursor.graphql",
                Map.of("cursor", cursor.getValue()),
                UserDTOConnection.class).block();
        assertThat(users.getEdges()).hasSize(1);
        assertThat(users.getEdges().get(0).getNode()).isEqualTo(allUsers.getEdges().get(1).getNode());
        assertThat(users.getPageInfo().isHasNextPage()).isFalse();
        assertThat(users.getPageInfo().isHasPreviousPage()).isTrue();
        assertThat(users.getPageInfo().getStartCursor()).isEqualTo(users.getEdges().get(0).getCursor());
        assertThat(users.getPageInfo().getEndCursor()).isEqualTo(users.getEdges().get(0).getCursor());
    }

}
