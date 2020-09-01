package exception;

public class NoMachinesFoundException extends Throwable {
    public NoMachinesFoundException() {
    }

    public NoMachinesFoundException(String message) {
        super(message);
    }
}
