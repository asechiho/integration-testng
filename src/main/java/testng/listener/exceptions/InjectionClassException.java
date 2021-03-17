package testng.listener.exceptions;

public class InjectionClassException extends RuntimeException {

    private static final String PREFIX = "Injection Error.";

    public InjectionClassException(String message) {
        super(PREFIX + message);
    }

}
