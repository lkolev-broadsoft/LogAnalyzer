package com.cisco;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class ArchiveFactory {

    protected static final String ARCHIVE_EXTENSION_ENDING = "((\\.zip)|(\\.tar\\.gz))";
    protected OldLogFileAnalyzerFactory logFileFactory = new OldLogFileAnalyzerFactory();
    protected LogFileAnalyzer logFileAnalyzer;

    protected Map analyzeLogFile(InputStream inputStream, int fileCount, String logFileName, String archiveFilePath, List<String> listOfFiles) {
        logFileAnalyzer = logFileFactory.getLogFileAnalyzer(logFileName);
        Map<String, Object> results = logFileAnalyzer.analyzeLog(inputStream, logFileName);
        //Take out write method outside analyzeLogFile method and call it in open method when analysis of the logs is done.
//        logFileAnalyzer.writeToOutputTxtFile((logFileAnalyzer.logType), archiveFilePath, results);
        //Return logFileAnalyer, in order to call write method on it.
        return results;
    }
}
