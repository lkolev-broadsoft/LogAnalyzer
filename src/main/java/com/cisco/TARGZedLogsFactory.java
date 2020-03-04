package com.cisco;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TARGZedLogsFactory implements TARGZFactory {

    protected List<String> listOfFiles = new ArrayList<>();

    OldLogFileAnalyzerFactory logFileFactory = new OldLogFileAnalyzerFactory();

    public TARGZedLogsFactory(String inputArchivePath) {
        open(inputArchivePath);
    }

    public void read(InputStream inputStream){
    }


     //Extract read(input Stream) method from open

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
                    //Code for extraction
                    LogFileAnalyzer logFileAnalyzer = logFileFactory.getLogFileAnalyzer(logFileName);
                    Map<String, Object> results = logFileAnalyzer.analyzeLog(tarArchiveInputStream, logFileName);
                    //logFileAnalyzer.writeToOutputTxtFile("result" + listOfFiles.get(filecount), results);
                    logFileAnalyzer.writeToOutputTxtFile(("result" + (logFileAnalyzer.getFileNames(listOfFiles).get(filecount))),results);
                    //Code for extraction
                    filecount++;
                }
            }
        } catch (IOException e) {
            logger.error("IOException while opening TAR archive.",e);
        }
    }
}
