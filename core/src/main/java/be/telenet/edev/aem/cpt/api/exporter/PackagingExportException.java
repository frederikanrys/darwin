package be.telenet.edev.aem.cpt.api.exporter;

import be.telenet.edev.aem.cpt.api.PackagingException;

public class PackagingExportException extends PackagingException {

    public PackagingExportException() {
        super();
    }

    public PackagingExportException(String message) {
        super(message);
    }

    public PackagingExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public PackagingExportException(Throwable cause) {
        super(cause);
    }
}