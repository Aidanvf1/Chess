package client;

import ui.Repl;

public class ClientMain {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port;
        try {
            port = parsePort(args);
        } catch (NumberFormatException ex) {
            System.err.println("Usage: ClientMain [port]");
            return;
        }
        new Repl(new ServerFacade(port)).run();
    }

    private static int parsePort(String[] args) {
        return args.length == 1 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
    }
}