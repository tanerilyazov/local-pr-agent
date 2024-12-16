package com.tanerilyazov.local_pr_agent.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.tanerilyazov.local_pr_agent.domain.model.CodeChange;
import com.tanerilyazov.local_pr_agent.domain.model.GitException;
import com.tanerilyazov.local_pr_agent.domain.model.ChangeType;
import com.tanerilyazov.local_pr_agent.domain.port.GitService;

@Service
public class ProcessGitService implements GitService {
    private static final int TIMEOUT_SECONDS = 30;
    
    @Override
    public List<CodeChange> getDiffBetweenBranches(String baseBranch, String compareBranch) {
        try {
            // First get the list of changed files
            List<String> changedFiles = getChangedFiles(baseBranch, compareBranch);
            List<CodeChange> changes = new ArrayList<>();

            for (String file : changedFiles) {
                ChangeType changeType = determineChangeType(file);
                String oldContent = "";
                String newContent = "";

                if (changeType != ChangeType.ADDED) {
                    oldContent = getFileContent(baseBranch, file);
                }
                if (changeType != ChangeType.DELETED) {
                    newContent = getFileContent(compareBranch, file);
                }

                changes.add(new CodeChange(file, oldContent, newContent, changeType));
            }

            return changes;
        } catch (Exception e) {
            throw new GitException("Failed to get diff between branches", e);
        }
    }

    private List<String> getChangedFiles(String baseBranch, String compareBranch) throws Exception {
        String command = String.format("git diff --name-status %s...%s", baseBranch, compareBranch);
        Process process = executeCommand(command);
        
        List<String> files = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip the status character (A/M/D) and whitespace
                files.add(line.substring(2));
            }
        }
        return files;
    }

    private ChangeType determineChangeType(String file) throws Exception {
        String command = String.format("git diff --name-status %s...%s -- %s", baseBranch, compareBranch, file);
        Process process = executeCommand(command);
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line == null) return ChangeType.MODIFIED;
            
            return switch (line.charAt(0)) {
                case 'A' -> ChangeType.ADDED;
                case 'D' -> ChangeType.DELETED;
                default -> ChangeType.MODIFIED;
            };
        }
    }

    private String getFileContent(String branch, String file) throws Exception {
        String command = String.format("git show %s:%s", branch, file);
        Process process = executeCommand(command);
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private Process executeCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new GitException("Command timed out: " + command);
        }
        
        if (process.exitValue() != 0) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                String error = reader.lines().reduce("", (a, b) -> a + "\n" + b);
                throw new GitException("Command failed: " + command + "\nError: " + error);
            }
        }
        
        return process;
    }
}