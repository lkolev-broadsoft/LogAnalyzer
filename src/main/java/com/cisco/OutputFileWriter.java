package com.cisco;

import java.util.Map;

public interface OutputFileWriter {


    void writeToOutputTxtFile(String filename, String archiveFilePath, Map<String, Object> inputMap);

    static String getFolderPath(String inputArchivePath) {
        return inputArchivePath.replaceAll("((\\.zip)|(\\.tar\\.gz))", "");
    }

}
