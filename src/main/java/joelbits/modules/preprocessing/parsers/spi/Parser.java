package joelbits.modules.preprocessing.parsers.spi;

import java.io.File;

public interface Parser {
    byte[] parse(File file) throws Exception;
    boolean hasBenchmarks(File file) throws Exception;
}
