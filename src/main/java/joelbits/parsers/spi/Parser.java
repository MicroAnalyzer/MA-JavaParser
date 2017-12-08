package joelbits.parsers.spi;

public interface Parser {
    void parse(String filePath);
    String type();
}
