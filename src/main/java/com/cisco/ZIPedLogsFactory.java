package com.cisco;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZIPedLogsFactory implements Factory{

    protected InputStream inputStream;

    protected List<String> listOfFiles = new ArrayList<>();

    protected Enumeration<? extends ZipEntry> entries;

    LogFileAnalyzerFactory logFileFactory = new LogFileAnalyzerFactory();

    @Override
    public void open(String inputArchiveFilePath, String logType) {
        try (ZipFile zipFile = new ZipFile(inputArchiveFilePath)){
            int filecount = 0;
            entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory() && entry.getName().contains(logType)){ //Use factory pattern to create IMPLogAnalyzer or PresenceLogAnalyzer
                    ZipEntry log = zipFile.getEntry(entry.getName());
                    listOfFiles.add(entry.getName());
                    inputStream = zipFile.getInputStream(log);
                    if(logType.equalsIgnoreCase("stats")){
                        logFileFactory.getLogFileAnalyzer(logType).writeToOutputTxtFile(logFileFactory.getLogFileAnalyzer(logType).analyzeLog(inputStream), "result_" + (listOfFiles.get(filecount)));
                    }
                    else {
                        logFileFactory.getLogFileAnalyzer(logType).writeToOutputTxtFile(("result" + (logFileFactory.getLogFileAnalyzer(logType).getFileNames(listOfFiles).get(filecount))), logFileFactory.getLogFileAnalyzer(logType).analyzeLog(inputStream));
                    }
                    filecount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
