# JavaParser

A Java file parser plug-in for MicroAnalyzer. The parser is used to convert a Java file into the entity domain model used
by MicroAnalyzer.

## How To Compile Sources

If you checked out the project from GitHub you can build the project with maven using:

```
mvn clean install
```

## Usage
Build the plugin jar and place it in the Java installation's */ext* folder. The return value of the overridden toString() method
corresponds to the parameter identifying the parser for MicroAnalyzer.
