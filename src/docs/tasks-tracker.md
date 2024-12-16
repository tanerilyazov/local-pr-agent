# MVP Implementation Tasks

## 1. Core Structure
```java
src/
  ├── domain/     # Core entities only
  ├── git/        # Simple diff reader
  ├── llm/        # Basic OpenAI client
  └── cli/        # Minimal CLI
```

## 2. Implementation Priority
1. Git Diff Reading 
- Get changes between branches
- Basic file content extraction

2. LLM Integration
- Simple OpenAI connection
- Single review prompt
- Basic error handling

3. CLI Interface
- Single command: analyze
- Branch parameter
- Basic output formatting

## 4. Testing
- Critical path tests only
- Sample diff scenarios
- Basic LLM mocking

## 5. Documentation
- README with setup steps
- API key configuration
- Basic usage examples