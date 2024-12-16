package com.tanerilyazov.local_pr_agent.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tanerilyazov.local_pr_agent.domain.model.CodeChange;
import com.tanerilyazov.local_pr_agent.domain.port.GitService;
import com.tanerilyazov.local_pr_agent.service.ProcessGitService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProcessGitServiceTest {
    @Autowired
    private ProcessGitService gitService;

    @Test
    void shouldGetDiffBetweenBranches() throws GitException {
        List<CodeChange> changes = gitService.getDiffBetweenBranches("master", "feature/part1");
        
        assertThat(changes).isNotEmpty();
        CodeChange firstChange = changes.get(0);
        assertThat(firstChange.filePath()).isNotEmpty();
        assertThat(firstChange.type()).isNotNull();
    }

    @Test
    void shouldHandleEmptyDiff() throws GitException {
        List<CodeChange> changes = gitService.getDiffBetweenBranches("master", "master");
        assertThat(changes).isEmpty();
    }
} 