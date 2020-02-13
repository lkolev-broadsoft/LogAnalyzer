package com.cisco;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZIPedLogsFactory  extends LogFileAnalyzerFactory {

    protected InputStream inputStream;

    protected List<String> listOfFiles = new ArrayList<>();

    protected Enumeration<? extends ZipEntry> entries;

    LogFileAnalyzerFactory logFileFactory = new LogFileAnalyzerFactory();

    protected void openZIPFile(String inputArchiveFilePath, String logType){
        try (ZipFile zipFile = new ZipFile(inputArchiveFilePath)){
            int filecount = 0;
            entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory() && entry.getName().contains(logType)){ //Use factory pattern to create IMPLogAnalyzer or PresenceLogAnalyzer
                    ZipEntry log = zipFile.getEntry(entry.getName());
                    listOfFiles.add(entry.getName());
                    inputStream = zipFile.getInputStream(log);
                    LogFileAnalyzer logFileAnalyzer = logFileFactory.getLogFileAnalyzer(logType);
                    LogFileAnalyzer.writeToOutputTxtFile(("result" + (logFileAnalyzer.getFileNames(listOfFiles).get(filecount))), logFileAnalyzer.analyzeLog(inputStream));
                    filecount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }


}
