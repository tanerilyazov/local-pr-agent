package com.tanerilyazov.local_pr_agent.domain.port;

import java.util.List;

import com.tanerilyazov.local_pr_agent.domain.model.CodeChange;

public interface GitService {
    List<CodeChange> getDiffBetweenBranches(String baseBranch, String compareBranch);
} 