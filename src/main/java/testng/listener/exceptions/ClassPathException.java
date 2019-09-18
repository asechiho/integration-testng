package testng.listener.exceptions;

public class ClassPathException extends RuntimeException {

    private static final String PREFIX = "Load class path error. ";

    public ClassPathException(String message) {
        super(PREFIX + message);
    }

}
