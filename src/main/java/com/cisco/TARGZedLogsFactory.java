package com.cisco;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;


import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TARGZedLogsFactory extends ArchiveFactory implements Openable {

    protected List<String> listOfFiles = new ArrayList<>();

    protected Map<String, Object> results;

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
            analyzeArchive(inputArchiveFilePath, tarArchiveInputStream);
            //logFileAnalyzer.writeToOutputTxtFile((logFileAnalyzer.logType), inputArchiveFilePath, results);
        } catch (IOException e) {
            LogFileAnalyzer.logger.error("IOException while opening TAR archive.",e);
            //Every class should have separate logger.
        }
    }

    private void analyzeArchive(String inputArchiveFilePath, TarArchiveInputStream tarArchiveInputStream) throws IOException {
        int fileCount = 0;
        TarArchiveEntry currentEntry;
//            checkForNestedArchives(tarArchiveInputStream, inputArchiveFilePath, currentEntry);
        while (((currentEntry = tarArchiveInputStream.getNextTarEntry()) != null)) {
                //checkForNestedArchives(tarArchiveInputStream, inputArchiveFilePath);
            if(!currentEntry.isDirectory()){
                String logFileName = getListOfFiles(currentEntry); //Refactored
                results = analyzeLogFile(tarArchiveInputStream, fileCount, logFileName, inputArchiveFilePath, listOfFiles);
                fileCount++;
            }
        }
        logFileAnalyzer.writeToOutputTxtFile((logFileAnalyzer.logType), inputArchiveFilePath, results);
    }

    private String getListOfFiles(TarArchiveEntry currentEntry) {
        String logFileName = currentEntry.getName();
        listOfFiles.add(logFileName);
        return logFileName;
    }

    private void checkForNestedArchives(TarArchiveInputStream inputStream, String inputFilePath) throws IOException {
        String fileName = inputStream.getNextEntry().getName();
        if(fileName.contains(".tar.gz")){
//           open(inputFilePath + File.separator + fileName);
            TarArchiveInputStream nestedTarAcrhiveInputStream = new TarArchiveInputStream(inputStream);
            String newInputFilePath = inputFilePath.replace(".tar.gz","") + File.separator + fileName;
            analyzeArchive( newInputFilePath ,nestedTarAcrhiveInputStream);
        }
    }

//    private void checkForNestedArchives(String inputFilePath, TarArchiveEntry currentEntry) throws IOException {
//        if (currentEntry.getName().toUpperCase().matches(ARCHIVE_EXTENSION_ENDING.toUpperCase())) {
//
//            decompressTar(inputFilePath,new File(inputFilePath + currentEntry.getName()));
//            Iterator it = FileUtils.iterateFiles(new File(inputFilePath + currentEntry.getName()), new SuffixFileFilter(".tar.gz"), null);
//            while(it.hasNext()){
//                System.out.println(((File) it.next()).getName());
//            }
//            //open(inputFilePath + File.separator + currentEntry);
//        }
//    }
//
//    private void decompressTar(String in, File out) throws IOException {
//        try (TarArchiveInputStream fin = new TarArchiveInputStream(new FileInputStream(in))){
//            TarArchiveEntry entry;
//            while ((entry = fin.getNextTarEntry()) != null) {
//                if (entry.isDirectory()) {
//                    continue;
//                }
//                File currentFile = new File(out, entry.getName());
//                File parent = currentFile.getParentFile();
//                if (!parent.exists()) {
//                    parent.mkdirs();
//                }
//                IOUtils.copy(fin, new FileOutputStream(currentFile));
//            }
//        }
//    }

}
