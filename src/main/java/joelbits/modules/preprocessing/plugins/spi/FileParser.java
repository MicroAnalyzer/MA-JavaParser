package joelbits.modules.preprocessing.plugins.spi;

import java.io.File;

public interface FileParser {
    byte[] parse(File file) throws Exception;
    boolean hasBenchmarks(File file) throws Exception;
}
