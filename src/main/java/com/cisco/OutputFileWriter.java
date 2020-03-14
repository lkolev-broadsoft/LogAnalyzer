package com.cisco;

import java.util.Map;

public interface OutputFileWriter {

    void writeToOutputTxtFile(String filename, Map<String, Object> inputMap, boolean isLastFile);

}
