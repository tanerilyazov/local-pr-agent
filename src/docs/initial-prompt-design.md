# Initial Prompt Design

## Base Prompt Template
```java
public class ReviewPrompt {
    private static final String REVIEW_TEMPLATE = """
        You are a senior Java developer reviewing code changes.
        Focus on:
        1. Code correctness
        2. Basic design issues
        3. Obvious bugs
        4. Critical performance issues

        Changes to review:
        %s

        Provide review in format:
        - [CRITICAL/IMPORTANT/MINOR]: Issue description
        - Suggestion: How to fix it
        
        Be concise and specific.
    """;
}
```

## Context Structure
```java
class ReviewContext {
    String changedCode;
    String fileType;     // .java, etc
    String methodName;   // if available
}
```

## Example Output
```
- [CRITICAL]: Method 'processUser' doesn't check for null input
- Suggestion: Add null check at method start

- [IMPORTANT]: Transaction not properly closed in catch block
- Suggestion: Use try-with-resources pattern
```
