# Code Review Assistant - Project Overview

## Vision
Create a high-quality, local-first code review tool that provides senior-level code reviews using LLMs, focusing initially on Java codebases with extensibility for other languages.

## Core Goals
1. Provide in-depth, actionable code reviews
   - Architecture impact analysis
   - Pattern recognition
   - Security vulnerabilities
   - Performance implications
   - Best practices enforcement

2. Support both local and cloud LLMs
   - OpenAI integration
   - Local LLM support (Ollama)
   - Flexible provider switching
   - Customizable prompts

3. Start with CLI, designed for future expansions
   - Clean architecture approach
   - Modular design
   - Platform-agnostic core
   - Extensible interfaces

4. Focus on Java code understanding
   - Deep Java parsing
   - Context-aware analysis
   - Framework recognition
   - Pattern detection

5. GitHub PR integration
   - PR comment creation
   - Diff analysis
   - Context gathering
   - Authentication handling

## Success Metrics
1. Review accuracy compared to senior developers
   - Blind testing with senior devs
   - Pattern recognition rate
   - Context understanding

2. Review completion time < 5 minutes
   - Fast local processing
   - Efficient API usage
   - Smart caching

3. Actionable suggestion rate > 80%
   - Clear, implementable feedback
   - Code examples
   - Documentation links
   - Rationale provided

4. False positive rate < 10%
   - Context validation
   - Pattern verification
   - Self-checking mechanisms
   
   # System Architecture Overview (Java-based)

## Core Components
```
┌─ Input Layer ──────┐   ┌─ Domain Core ───────┐   ┌─ Output Layer ────┐
│ - Git Diff Parser  │   │ - Review Engine     │   │ - GitHub Comments │
│ - Context Gatherer │ → │ - Analysis Pipeline │ → │ - Console Output  │
│ - Config Loader    │   │ - LLM Orchestrator  │   │ - JSON Export     │
└──────────────────┘    └──────────────────────┘   └──────────────────┘
```

## Key Services

### 1. Code Analysis Service
- JavaParser for AST
- Symbol resolution
- Type inference
- Pattern detection
- Context collection

### 2. LLM Integration Service
- Provider abstraction
- Prompt management
- Response processing
- Error handling
- Retry logic

### 3. Review Pipeline
```
Code Changes → Context Gathering → Analysis → LLM Processing → Output Formation
```

# ADR: Code Review Pipeline Design

## Status
Accepted

## Context
Need modular, extensible pipeline for processing code reviews through multiple stages.

## Pipeline Structure
```
┌─ Input ─────┐  ┌─ Analysis ───┐  ┌─ Review ────┐  ┌─ Output ────┐
│ Diff Load   │→ │ Parse AST    │→ │ LLM Review  │→ │ Format      │
│ Get Context │→ │ Symbol Solve │→ │ Validate    │→ │ Prioritize  │
└────────────┘  └─────────────┘  └────────────┘  └────────────┘
```

## Key Components

### 1. ReviewPipeline Interface
```java
public interface ReviewPipeline {
    ReviewResult process(ReviewRequest request);
    void addPreProcessor(Processor p);
    void addPostProcessor(Processor p);
}
```

### 2. Stage Definitions
- Each stage is isolated
- Clear input/output contracts
- Configurable/skippable
- Parallel execution where possible

## Consequences
+ Modular design enables easy extensions
+ Clear separation of concerns
- Need careful error handling between stages

# ADR: LLM Integration Strategy

## Status
Accepted

## Context
Need flexible, provider-agnostic LLM integration supporting both cloud and local models.

## Design
```java
public interface LLMProvider {
    ReviewResponse analyze(ReviewRequest request);
    boolean supportsStreaming();
    ModelCapabilities getCapabilities();
}
```

## Implementation

### 1. Provider Registry
```java
@Service
public class LLMRegistry {
    private Map<String, LLMProvider> providers;
    private LLMProvider defaultProvider;
    
    public LLMProvider getProvider(String name) {...}
    public LLMProvider getDefault() {...}
}
```

### 2. Configuration
```hocon
llm {
  default-provider = "openai"
  openai {
    model = "gpt-4"
    temperature = 0.3
    max-tokens = 2000
  }
  local {
    url = "http://localhost:11434"
    model = "codellama"
  }
}
```

### 3. Prompt Management
- Templated prompts
- Context windowing
- Token optimization
- Caching strategy

# ADR: Review Quality Assurance

## Status
Accepted

## Context
Need to ensure consistent, high-quality code reviews that match senior developer standards.

## Quality Control Mechanisms

### 1. Review Validation
```java
public class ReviewValidator {
    boolean validateSuggestion(Suggestion s) {...}
    double calculateConfidenceScore(Review r) {...}
    List<Issue> detectFalsePositives(Review r) {...}
}
```

### 2. Quality Metrics
- Suggestion actionability
- Code compilability
- Pattern consistency
- Reference accuracy
- Context relevance

### 3. Review Categories
```java
enum ReviewSeverity {
    CRITICAL,    // Must fix
    MAJOR,       // Should fix
    MINOR,       // Consider fixing
    INFO         // Best practice
}
```

### 4. Validation Steps
1. Pre-review context validation
2. Post-review suggestion verification
3. Pattern matching against known good/bad practices
4. Cross-reference with project standards

# ADR: Meta-Review Validation System

## Status
Accepted

## Context
Need self-validating review system to ensure quality and completeness of initial reviews.

## Design
```
┌─ Initial Review ─┐   ┌─ Meta Review ────────┐   ┌─ Final Output ─┐
│ Regular Pipeline │ → │ Validation           │ → │ Merged Review  │
└────────────────┘   │ Completeness Check   │   └──────────────┘
                     │ Additional Analysis   │
                     └───────────────────────┘
```

### Meta-Reviewer Interface
```java
public interface MetaReviewer {
    MetaReviewResult validate(Review initialReview);
    boolean needsAdditionalReview(MetaReviewResult result);
    Review mergeReviews(Review initial, Review additional);
}
```

### Validation Criteria
1. Completeness Check
- All critical areas covered
- Pattern consistency
- Depth of analysis

2. Quality Gates
- Confidence scores
- Coverage metrics
- Consistency checks

3. Enhancement Triggers
```java
enum EnhancementTrigger {
    MISSING_CONTEXT,
    LOW_CONFIDENCE,
    INCONSISTENT_SUGGESTIONS,
    INCOMPLETE_COVERAGE
}
```

# ADR: Review Output Strategy

## Status
Accepted

## Context
Need flexible, structured output system that can adapt to different presentation needs while maintaining consistency.

## Design
```java
public interface ReviewOutput {
    void present(ReviewResult review);
    void groupByPriority();
    void groupByFile();
    void addMetadata(Map<String, Object> meta);
}
```

## Output Formats
```
┌─ Core Result ──┐   ┌─ Formatters ───┐   ┌─ Destinations ─┐
│ Review Data    │ → │ GitHub Format  │ → │ PR Comments    │
│ Meta Review    │ → │ CLI Format     │ → │ Console        │
│ Priorities     │ → │ JSON Format    │ → │ File Output    │
└───────────────┘   └───────────────┘   └───────────────┘
```

### Structured Output Model
```java
class ReviewOutput {
    List<FileReview> fileReviews;
    MetaReview metaReview;
    QualityMetrics metrics;
    List<ActionItem> prioritizedActions;
}
```

### Key Features
- Consistent formatting across outputs
- Priority-based grouping
- File-based organization
- Action item tracking

# ADR: Configuration Management

## Status
Accepted

## Context
Need flexible, hierarchical configuration system that supports multiple environments and user preferences.

## Design
```java
public class ReviewConfig {
    private static final String CONFIG_HIERARCHY = """
        1. CLI arguments
        2. Project .reviewrc
        3. User home config
        4. System defaults
    """;
}
```

## Configuration Structure
```hocon
review {
  # Core settings
  language = "java"
  depth = "detailed"  # quick|detailed|comprehensive
  
  # Review focus
  priorities {
    architecture = true
    security = true
    performance = true
    style = true
  }
  
  # LLM settings
  llm = ${llm-defaults} {
    provider = "openai"
    temperature = 0.3
  }
  
  # Output settings
  output {
    format = "github"
    group-by = "priority"  # priority|file|type
  }
}
```

### Features
1. Environment awareness
2. Override mechanics
3. Validation rules
4. Hot reload support

# ADR: Error Handling Strategy

## Status
Accepted

## Context
Need robust error handling across all pipeline stages with graceful degradation and clear user feedback.

## Design
```java
public sealed interface ReviewError {
    record LLMError(String provider, String message) implements ReviewError {}
    record ParseError(String file, String details) implements ReviewError {}
    record ConfigError(String path, String reason) implements ReviewError {}
    record GithubError(int status, String message) implements ReviewError {}
}
```

## Error Handling Strategy
```
┌─ Error Types ────┐   ┌─ Recovery Actions ─┐   ┌─ User Feedback ─┐
│ Recoverable     │ → │ Retry Logic       │ → │ CLI Messages   │
│ Non-recoverable │ → │ Fallback Path     │ → │ Error Logs     │
│ Partial Failure │ → │ Partial Results   │ → │ Debug Info     │
└────────────────┘   └──────────────────┘   └───────────────┘
```

### Key Features
1. Graceful Degradation
2. Clear Error Messages
3. Debug Information
4. Recovery Strategies
5. Partial Results Support

# ADR: Error Handling Strategy

## Status
Accepted

## Context
Need robust error handling across all pipeline stages with graceful degradation and clear user feedback.

## Design
```java
public sealed interface ReviewError {
    record LLMError(String provider, String message) implements ReviewError {}
    record ParseError(String file, String details) implements ReviewError {}
    record ConfigError(String path, String reason) implements ReviewError {}
    record GithubError(int status, String message) implements ReviewError {}
}
```

## Error Handling Strategy
```
┌─ Error Types ────┐   ┌─ Recovery Actions ─┐   ┌─ User Feedback ─┐
│ Recoverable     │ → │ Retry Logic       │ → │ CLI Messages   │
│ Non-recoverable │ → │ Fallback Path     │ → │ Error Logs     │
│ Partial Failure │ → │ Partial Results   │ → │ Debug Info     │
└────────────────┘   └──────────────────┘   └───────────────┘
```

### Key Features
1. Graceful Degradation
2. Clear Error Messages
3. Debug Information
4. Recovery Strategies
5. Partial Results Support


