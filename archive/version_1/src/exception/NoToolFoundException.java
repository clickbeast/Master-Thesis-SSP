package exception;

public class NoToolFoundException extends Throwable {
    public NoToolFoundException() {
    }

    public NoToolFoundException(String message) {
        super(message);
    }
}
