package com.cisco;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectoryFactory implements ArchiveFactory {

    protected List<String> listOfFiles = new ArrayList<>();

    protected OldLogFileAnalyzerFactory logFileFactory = new OldLogFileAnalyzerFactory();

    protected FileInputStream fileInputStream;

//    @Override
//    public void open(String inputArchiveFilePath) {
//        Path filePath = Paths.get(inputArchiveFilePath);
//        try (Stream<Path> directoryPathStream = Files.walk(filePath)) {
//            int filecount = 0;
//
//
////            while (entries.hasMoreElements()) {
////                ZipEntry entry = entries.nextElement();
////                if (!entry.isDirectory()) {
////                    String logFileName = entry.getName();
////                    ZipEntry zipEntry = zipFile.getEntry(logFileName);
////                    listOfFiles.add(logFileName);
////                    inputStream = zipFile.getInputStream(zipEntry);
////                    LogFileAnalyzer logFileAnalyzer = logFileFactory.getLogFileAnalyzer(logFileName);
////                    Map<String, Object> results = logFileAnalyzer.analyzeLog(inputStream);
////                    logFileAnalyzer.writeToOutputTxtFile(("result" + (logFileAnalyzer.getFileNames(listOfFiles).get(filecount))), results);
////                    filecount++;
////                }
////            }
//        } catch (IOException e) {
//            //        e.printStackTrace();
//            logger.error("IOException while opening ZIP archive.", e);
//        }
//    }

    @Override
    public void open(String inputArchiveFilePath) {
        Path directoryPath = Paths.get(inputArchiveFilePath);
        int filecount = 0;
        try(DirectoryStream<Path> direcotryPathStream = Files.newDirectoryStream(directoryPath, "*.{log,txt}")) {
            for(Path path : direcotryPathStream){
                String logFileName = String.valueOf(path.getFileName());
                listOfFiles.add(logFileName);
                fileInputStream = new FileInputStream(path.toFile());
                LogFileAnalyzer logFileAnalyzer = logFileFactory.getLogFileAnalyzer(logFileName);
                Map<String, Object> results = logFileAnalyzer.analyzeLog(fileInputStream);
                logFileAnalyzer.writeToOutputTxtFile(("result" + (logFileAnalyzer.getFileNames(listOfFiles).get(filecount))), results);
                filecount++;
            }
        } catch (IOException e) {
            logger.error("IOException while opening directory.",e);
//            e.printStackTrace();
        }

    }
}
