package exception;

public class SetupNotCompleteException extends Throwable {
    public SetupNotCompleteException() {
        super();
    }

    public SetupNotCompleteException(String message) {
        super(message);
    }
}
