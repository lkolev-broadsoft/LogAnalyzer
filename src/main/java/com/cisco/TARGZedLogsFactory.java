package com.cisco;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TARGZedLogsFactory implements TARGZFactory {

    protected List<String> listOfFiles = new ArrayList<>();

    OldLogFileAnalyzerFactory logFileFactory = new OldLogFileAnalyzerFactory();

    @Override
    public void open(String inputArchiveFilePath) {
        try (FileInputStream fileInputStream = new FileInputStream(inputArchiveFilePath);
             GzipCompressorInputStream gzipInputStream = new GzipCompressorInputStream(fileInputStream);
             TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream)) {
            int filecount = 0;
            TarArchiveEntry currentEntry;
            while (((currentEntry = tarArchiveInputStream.getNextTarEntry()) != null)) {
                if(!currentEntry.isDirectory()){
                    String logFileName = currentEntry.getName();
                    listOfFiles.add(logFileName);
                    LogFileAnalyzer logFileAnalyzer = logFileFactory.getLogFileAnalyzer(logFileName);
                    Map<String, Object> results = logFileAnalyzer.analyzeLog(tarArchiveInputStream);
                    logFileAnalyzer.writeToOutputTxtFile("result" + listOfFiles.get(filecount), results);
                    filecount++;
                }
            }
        } catch (IOException e) {
            logger.error("IOException while opening TAR archive.",e);
        }
    }
}
