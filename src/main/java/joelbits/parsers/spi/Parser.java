package joelbits.parsers.spi;

import java.io.File;

public interface Parser {
    void parse(File file);
    String type();
}
