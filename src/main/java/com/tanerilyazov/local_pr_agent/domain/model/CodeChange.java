package com.tanerilyazov.local_pr_agent.domain.model;

public record CodeChange(
    String filePath,
    String oldContent,
    String newContent,
    ChangeType changeType
) {} 