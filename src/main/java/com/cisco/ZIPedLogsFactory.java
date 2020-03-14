package com.cisco;


import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZIPedLogsFactory extends ArchiveFactory implements Openable {

    protected InputStream inputStream;

    protected List<String> listOfFiles = new ArrayList<>();

    protected Enumeration<? extends ZipEntry> entries;

    protected boolean isLastFile = false;

    public ZIPedLogsFactory(String inputArchivePath) {
        open(inputArchivePath);
    }

    @Override
    public void open(String inputArchiveFilePath) {
        try (ZipFile zipFile = new ZipFile(inputArchiveFilePath)){
            int fileCount = 0;
            entries = zipFile.entries();
            boolean isLastFile;
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory()){
                    String logFileName = entry.getName();
                    ZipEntry zipEntry = zipFile.getEntry(logFileName);
                    listOfFiles.add(logFileName);
                    inputStream = zipFile.getInputStream(zipEntry);
                    isLastFile = checkForLastFile(entries);
                    analyzeLogFile(inputStream,fileCount,logFileName, listOfFiles, isLastFile);
                    fileCount++;
                }
            }
        } catch (IOException e) {
            LogFileAnalyzer.logger.error("IOException while opening ZIP archive.",e);
        }
    }

    private boolean checkForLastFile(Enumeration<? extends ZipEntry> entries) throws IOException {
        return !entries.hasMoreElements();
    }

}
