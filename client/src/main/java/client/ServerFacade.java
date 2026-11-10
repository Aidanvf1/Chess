package client;

import com.google.gson.Gson;
import model.AuthData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public void clear() throws ResponseException {
        sendRequest("DELETE", "/db", null, null, null);
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        var body = Map.of("username", username, "password", password, "email", email);
        return sendRequest("POST", "/user", body, null, AuthData.class);
    }

    private <T> T sendRequest(String method, String path, Object body, String authToken,
                              Class<T> responseType) throws ResponseException {
        HttpRequest request = buildRequest(method, path, body, authToken);
        HttpResponse<String> response = send(request);
        if (!isSuccessful(response.statusCode())) {
            throw new ResponseException(messageFor(response.statusCode()));
        }
        return responseType == null ? null : gson.fromJson(response.body(), responseType);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var builder = HttpRequest.newBuilder(URI.create(serverUrl + path));
        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.method(method, HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
            builder.header("Content-Type", "application/json");
        }
        if (authToken != null) {
            builder.header("authorization", authToken);
        }
        return builder.build();
    }

    private HttpResponse<String> send(HttpRequest request) throws ResponseException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException ex) {
            throw new ResponseException("Could not reach the server. Is it running?");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseException("The request was interrupted. Please try again.");
        }
    }

    private boolean isSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    private String messageFor(int statusCode) {
        return switch (statusCode) {
            case 400 -> "That wasn't a valid request. Check what you typed and try again.";
            case 401 -> "Not authorized. Check your username and password, or log in again.";
            case 403 -> "That is already taken.";
            default -> "The server ran into a problem. Please try again.";
        };
    }
}
