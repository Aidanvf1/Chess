package service;

public class UnauthorizedException extends ServiceException {
    public UnauthorizedException() {
        super("Error: unauthorized", 401);
    }
}