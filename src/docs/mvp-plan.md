# Streamlined MVP Plan

## Core MVP Features (2 weeks)
1. Essential Pipeline:
```
Git Branch Diff → LLM Review → Console Output
```

2. Must-Have Functions:
- Read local git branch changes
- Basic OpenAI integration (single prompt)
- Simple text output to console
- Basic Java code understanding
- Configuration file for API key

## Example Usage
```bash
review analyze --branch feature/xyz
```

## Success Criteria
1. Can understand Java code changes
2. Provides useful feedback
3. < 1 min review time
4. Works locally

## Out of Scope for MVP
- GitHub integration
- Meta review
- Multiple LLM providers
- Complex configurations
- Advanced analysis

# MVP Implementation Tasks

## 1. Core Structure (2 days)
```java
src/
  ├── domain/     # Core entities only
  ├── git/        # Simple diff reader
  ├── llm/        # Basic OpenAI client
  └── cli/        # Minimal CLI
```

## 2. Implementation Priority
1. Git Diff Reading (1 day)
- Get changes between branches
- Basic file content extraction

2. LLM Integration (2 days)
- Simple OpenAI connection
- Single review prompt
- Basic error handling

3. CLI Interface (1 day)
- Single command: analyze
- Branch parameter
- Basic output formatting

## 4. Testing (1 day)
- Critical path tests only
- Sample diff scenarios
- Basic LLM mocking

## 5. Documentation
- README with setup steps
- API key configuration
- Basic usage examples

# Post-MVP Roadmap

## Phase 1: Enhanced Review Quality
1. Meta-review system
2. Multiple LLM prompts for different aspects
3. Better Java code understanding
4. False positive reduction

## Phase 2: Integration
1. GitHub PR support
2. Local LLM support
3. Multiple output formats

## Phase 3: Advanced Features
1. Custom rule sets
2. Team conventions support
3. Historical analysis

Now, let's break down into User Stories:

# User Stories - Phase 1

## US1: Basic Code Review
```
As a developer
I want to review my branch changes
So that I can improve code quality before PR

Acceptance Criteria:
1. Command works: `review analyze --branch feature/xyz`
2. Shows meaningful code issues
3. Provides actionable suggestions
4. Completes in under 1 minute

Test Cases:
- Review branch with single file change
- Review multiple file changes
- Handle empty/invalid branch
- Test different Java constructs
```