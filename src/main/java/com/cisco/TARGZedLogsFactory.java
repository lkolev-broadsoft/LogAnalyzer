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

public class TARGZedLogsFactory implements Openable {

    protected List<String> listOfFiles = new ArrayList<>();

    OldLogFileAnalyzerFactory logFileFactory = new OldLogFileAnalyzerFactory();

    public TARGZedLogsFactory(String inputArchivePath) {
        open(inputArchivePath);
    }

    public void read(InputStream inputStream){
    }

    protected boolean isLastFile;

     //Extract read(input Stream) method from open

    @Override
    public void open(String inputArchiveFilePath) {
        try (FileInputStream fileInputStream = new FileInputStream(inputArchiveFilePath);
             GzipCompressorInputStream gzipInputStream = new GzipCompressorInputStream(fileInputStream);
             TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream)) {
            int fileCount = 0;
            TarArchiveEntry currentEntry;
            while (((currentEntry = tarArchiveInputStream.getNextTarEntry()) != null)) {
                if(!currentEntry.isDirectory()){
                    String logFileName = getListOfFiles(currentEntry); //Refactored
                    //isLastFile = (tarArchiveInputStream.getNextTarEntry() == null); //Check if it is the last file in the archive
                    //Code for extraction
                    analyzeLogFile(tarArchiveInputStream, fileCount, logFileName, isLastFile);
                    //Code for extraction
                    fileCount++;
                }
            }
        } catch (IOException e) {
            LogFileAnalyzer.logger.error("IOException while opening TAR archive.",e);

            //Every class should have separate logger.
        }
    }

    private void analyzeLogFile(TarArchiveInputStream tarArchiveInputStream, int fileCount, String logFileName, boolean isLastFileInArchive) {
        LogFileAnalyzer logFileAnalyzer = logFileFactory.getLogFileAnalyzer(logFileName);
        Map<String, Object> results = logFileAnalyzer.analyzeLog(tarArchiveInputStream, logFileName);
        logFileAnalyzer.writeToOutputTxtFile(("result" + (logFileAnalyzer.getFileNames(listOfFiles).get(fileCount))),results, isLastFileInArchive);
        //Add logic in StatsLogAnalyzer, analyzeLog method to check if it is the last file in the archive or in the directory and write the accumulated statistics to file.
    }

    private String getListOfFiles(TarArchiveEntry currentEntry) {
        String logFileName = currentEntry.getName();
        listOfFiles.add(logFileName);
        return logFileName;
    }
}
