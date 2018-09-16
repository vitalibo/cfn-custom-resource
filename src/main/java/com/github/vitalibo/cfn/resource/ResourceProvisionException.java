package com.github.vitalibo.cfn.resource;

public class ResourceProvisionException extends RuntimeException {

    public ResourceProvisionException() {
        super();
    }

    public ResourceProvisionException(String message) {
        super(message);
    }

    public ResourceProvisionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceProvisionException(Throwable cause) {
        super(cause);
    }

}