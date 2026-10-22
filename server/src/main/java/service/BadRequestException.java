package service;

public class BadRequestException extends ServiceException {
    public BadRequestException() {
        super("Error: bad request", 400);
    }
}