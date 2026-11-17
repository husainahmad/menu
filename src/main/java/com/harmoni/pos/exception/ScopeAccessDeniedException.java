package com.harmoni.pos.exception;

/**
 * Thrown when a caller asks for an organisation resource outside the brand,
 * chain or store scope granted by their RBAC assignments.
 */
public class ScopeAccessDeniedException extends RuntimeException {

    /**
     * Creates the exception with a human readable reason.
     *
     * @param message the reason shown to the caller
     */
    public ScopeAccessDeniedException(String message) {
        super(message);
    }
}
