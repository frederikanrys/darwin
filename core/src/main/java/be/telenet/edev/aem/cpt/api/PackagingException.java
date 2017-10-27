package be.telenet.edev.aem.cpt.api;

public class PackagingException extends Exception {

    public PackagingException() {
        super();
    }

    public PackagingException(String message) {
        super(message);
    }

    public PackagingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PackagingException(Throwable cause) {
        super(cause);
    }
}