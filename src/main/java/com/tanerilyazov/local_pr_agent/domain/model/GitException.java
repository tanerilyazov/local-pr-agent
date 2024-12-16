package com.tanerilyazov.local_pr_agent.domain.model;

public class GitException extends RuntimeException {
    public GitException(String message) {
        super(message);
    }

    public GitException(String message, Throwable cause) {
        super(message, cause);
    }
} 
