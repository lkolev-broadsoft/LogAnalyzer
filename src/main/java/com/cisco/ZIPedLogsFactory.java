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

    @Override
    public void open(String inputArchiveFilePath) {
        try (ZipFile zipFile = new ZipFile(inputArchiveFilePath)){
            int filecount = 0;
            entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
               //// logFileFactory.getLogType(entry.getName());
               // if(!entry.isDirectory() && entry.getName().contains(logType)){ //Use factory pattern to create IMPLogAnalyzer or PresenceLogAnalyzer
                if(!entry.isDirectory()){
                    String logFileName = entry.getName();
                    ZipEntry zipEntry = zipFile.getEntry(logFileName);
                    listOfFiles.add(logFileName);
                    inputStream = zipFile.getInputStream(zipEntry);
                    Map<String, Object> results = logFileFactory.getLogFileAnalyzer(logFileName).analyzeLog(inputStream);
                    logFileFactory.getLogFileAnalyzer(logFileName).writeToOutputTxtFile(("result" + (logFileFactory.getLogFileAnalyzer(logFileName).getFileNames(listOfFiles).get(filecount))),results);
                    filecount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
