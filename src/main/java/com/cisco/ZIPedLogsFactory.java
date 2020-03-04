package com.cisco;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZIPedLogsFactory implements ZIPFactory {

    protected InputStream inputStream;

    protected List<String> listOfFiles = new ArrayList<>();

    protected Enumeration<? extends ZipEntry> entries;

    OldLogFileAnalyzerFactory logFileFactory = new OldLogFileAnalyzerFactory();

    public ZIPedLogsFactory(String inputArchivePath) {
        open(inputArchivePath);
    }

    @Override
    public void open(String inputArchiveFilePath) {
        try (ZipFile zipFile = new ZipFile(inputArchiveFilePath)){
            int filecount = 0;
            entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory()){
                    String logFileName = entry.getName();
                    ZipEntry zipEntry = zipFile.getEntry(logFileName);
                    listOfFiles.add(logFileName);
                    inputStream = zipFile.getInputStream(zipEntry);
                    //Code for extraction
                    LogFileAnalyzer logFileAnalyzer = logFileFactory.getLogFileAnalyzer(logFileName);
                    Map<String, Object> results = logFileAnalyzer.analyzeLog(inputStream, logFileName);
                    logFileAnalyzer.writeToOutputTxtFile(("result" + (logFileAnalyzer.getFileNames(listOfFiles).get(filecount))),results);
                    //Code for extraction
                    filecount++;
                }
            }
        } catch (IOException e) {
            logger.error("IOException while opening ZIP archive.",e);
        }
    }


}
