# SystemVerilog Importer Configuration

The SystemVerilog Importer plugin supports configuration through system properties using the `oft.sv` prefix.

## Configuration Properties

### `oft.sv.process_title_description`

**Type:** Boolean  
**Default:** `false`  
**Description:** Enable processing of title and description from SystemVerilog comments.

When enabled, the importer will parse comments in the format:

```systemverilog
// [req->dsn~feature-id~1] My Title: This is the description
```

And extract:

- **Title:** "My Title"
- **Description:** "This is the description"

## Usage Examples

### Command Line

Set system properties when running your application:

```bash
# Enable title and description processing
java -Doft.sv.process_title_description=true -jar your-application.jar

# Disable title and description processing (default)
java -Doft.sv.process_title_description=false -jar your-application.jar
```

### Environment Variables

While system properties are the primary configuration mechanism, you can also set them programmatically:

```java
// Enable before creating the importer
System.setProperty("oft.sv.process_title_description", "true");

// Create importer - will automatically use the configuration
SVImporter importer = new SVImporter(file, listener);
```

### Gradle Build Script

Set properties in your `build.gradle`:

```gradle
test {
    systemProperty 'oft.sv.process_title_description', 'true'
}

run {
    systemProperty 'oft.sv.process_title_description', 'true'
}
```

### IDE Configuration

In IntelliJ IDEA or Eclipse, add VM options:

```bash
-Doft.sv.process_title_description=true
```

## Configuration Precedence

1. System properties (highest priority)
2. Default values (lowest priority)

## Backward Compatibility

All existing constructor overloads remain available for programmatic control:

```java
// Use system property configuration
SVImporter importer = new SVImporter(file, listener);

// Override system property configuration
SVImporter importer = new SVImporter(file, listener, true);
```