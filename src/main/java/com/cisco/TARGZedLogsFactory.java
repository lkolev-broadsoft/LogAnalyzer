package com.cisco;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TARGZedLogsFactory extends LogFileAnalyzerFactory {

    protected List<String> listOfFiles = new ArrayList<>();

    LogFileAnalyzerFactory logFileFactory = new LogFileAnalyzerFactory();

    protected void openTarGZFile(String inputArchiveFilePath, String logType) {
        try (FileInputStream fileInputStream = new FileInputStream(inputArchiveFilePath);
             GzipCompressorInputStream gzipInputStream = new GzipCompressorInputStream(fileInputStream);
             TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream)){
            int filecount = 0;
            TarArchiveEntry currentEntry;
            while (((currentEntry = tarArchiveInputStream.getNextTarEntry()) != null)) {
                if (!currentEntry.isDirectory() && currentEntry.getName().contains(logType)) {
                    listOfFiles.add(currentEntry.getName());
                    Map<String, Long> results = logFileFactory.getLogFileAnalyzer(logType).analyzeLog(tarArchiveInputStream);
                    logFileFactory.getLogFileAnalyzer(logType).writeToOutputTxtFile("result" + listOfFiles.get(filecount), results);
                    filecount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}