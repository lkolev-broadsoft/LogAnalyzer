package com.cisco;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class ArchiveFactory {

    protected OldLogFileAnalyzerFactory logFileFactory = new OldLogFileAnalyzerFactory();

    protected void analyzeLogFile(InputStream inputStream, int fileCount, String logFileName, String archiveFilePath, List<String> listOfFiles) {
        LogFileAnalyzer logFileAnalyzer = logFileFactory.getLogFileAnalyzer(logFileName);
        Map<String, Object> results = logFileAnalyzer.analyzeLog(inputStream, logFileName);
        logFileAnalyzer.writeToOutputTxtFile(("result" + (logFileAnalyzer.getFileNames(listOfFiles).get(fileCount))), archiveFilePath, results);
        //Add logic in StatsLogAnalyzer, analyzeLog method to check if it is the last file in the archive or in the directory and write the accumulated statistics to file.
    }
}
