package com.cisco;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TARGZedLogsFactory extends ArchiveFactory implements Openable {

    protected List<String> listOfFiles = new ArrayList<>();

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
//            checkForNestedArchives(tarArchiveInputStream, inputArchiveFilePath);
            int fileCount = 0;
            TarArchiveEntry currentEntry;
            while (((currentEntry = tarArchiveInputStream.getNextTarEntry()) != null)) {
                if(!currentEntry.isDirectory()){
                    String logFileName = getListOfFiles(currentEntry); //Refactored
                    analyzeLogFile(tarArchiveInputStream, fileCount, logFileName,listOfFiles);
                    fileCount++;
                }
            }
        } catch (IOException e) {
            LogFileAnalyzer.logger.error("IOException while opening TAR archive.",e);

            //Every class should have separate logger.
        }
    }

    private String getListOfFiles(TarArchiveEntry currentEntry) {
        String logFileName = currentEntry.getName();
        listOfFiles.add(logFileName);
        return logFileName;
    }

//    private void checkForNestedArchives(TarArchiveInputStream inputStream, String inputFilePath) throws IOException {
//        String fileName = inputStream.getNextEntry().getName();
//        if(fileName.contains(".tar.gz")){
//           open(inputFilePath + "\\" + fileName);
//        }
//    }

}
