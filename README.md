A tiny service leveraging `graphql-java-kickstart` library and Spring Boot. It provides a sample GraphQL API following relay specification with pagination. There is one query `users` that returns all the available users, and one mutation `createAuthToken` that creates an opaque auth token. 
GraphQL schema is defined in `src/resources/schema.grahqls`.

The token is a base64 encoded SHA-2 hash of a JSON object contatining user ID, password hash, and token expiration date. Based on this data, validation and revocation of the token can be implemented.

### How to build and run

To build the project:
```
$ ./gradlw :build
```

To run the project:
```
$ ./gradlew :bootRun
```

Running the app will make GraphQL API available under `http://localhost:8080/graphql`, it will also run Graph*i*QL under `http://localhost:8080/graphiql`. The graphql schema is defined in `/src/resources/schema.graphqls`. The sample queries can be found in `src/test/resources`.

