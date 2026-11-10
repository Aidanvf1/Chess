package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clear();
    }

    @Test
    public void clearRemovesExistingUsers() throws ResponseException {
        facade.register("hobbs", "password", "hobbs@email.com");

        facade.clear();

        Assertions.assertDoesNotThrow(() -> facade.register("hobbs", "password", "hobbs@email.com"));
    }

    @Test
    public void clearServerUnreachable() {
        Assertions.assertThrows(ResponseException.class, () -> new ServerFacade(1).clear());
    }

    @Test
    public void registerSuccess() throws ResponseException {
        AuthData authData = facade.register("hobbs", "password", "hobbs@email.com");

        Assertions.assertTrue(authData.authToken().length() > 10);
        Assertions.assertEquals("hobbs", authData.username());
    }

    @Test
    public void registerDuplicateUsername() throws ResponseException {
        facade.register("hobbs", "password", "hobbs@email.com");

        Assertions.assertThrows(ResponseException.class,
                () -> facade.register("hobbs", "ellie123", "ellie@email.com"));
    }
}