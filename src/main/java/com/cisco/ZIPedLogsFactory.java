package com.cisco;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZIPedLogsFactory extends ArchiveFactory implements Openable {

    protected InputStream inputStream;

    protected List<String> listOfFiles = new ArrayList<>();

    protected Enumeration<? extends ZipEntry> entries;

    protected Map<String, Object> results;

    public ZIPedLogsFactory(String inputArchivePath) {
        open(inputArchivePath);
    }

    @Override
    public void open(String inputArchiveFilePath) {
        try (ZipFile zipFile = new ZipFile(inputArchiveFilePath)){
            int fileCount = 0;
            entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory()){
                    String logFileName = entry.getName();
                    ZipEntry zipEntry = zipFile.getEntry(logFileName);
                    listOfFiles.add(logFileName);
                    inputStream = zipFile.getInputStream(zipEntry);
                    results  = analyzeLogFile(inputStream,fileCount,logFileName, inputArchiveFilePath, listOfFiles);
                    //logFileAnalyzer.writeToOutputTxtFile((logFileAnalyzer.logType), inputArchiveFilePath, results);
                    fileCount++;
                }
            }
            //Write results to output file after the analysis of the logs is done.
            logFileAnalyzer.writeToOutputTxtFile((logFileAnalyzer.logType), inputArchiveFilePath, results);
        } catch (IOException e) {
            LogFileAnalyzer.logger.error("IOException while opening ZIP archive.",e);
        }
    }
}
