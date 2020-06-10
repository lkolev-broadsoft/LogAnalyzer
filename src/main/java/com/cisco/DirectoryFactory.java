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

public class DirectoryFactory extends ArchiveFactory implements Openable {

    protected List<String> listOfFiles = new ArrayList<>();

    protected FileInputStream fileInputStream;

    protected Map<String, Object> results;

    public DirectoryFactory(String inputPath) {
        //Check if there are archives in the provided directory and use recursion to call analyser
        //Problem that it goes in the Results directories.
        //Maybe write a check to exclude results folder
        checkForArchivesInDirectory(inputPath);
        //open(inputPath);
    }

    @Override
    public void open(String inputArchiveFilePath) {
        Path directoryPath = Paths.get(inputArchiveFilePath);
        int filecount = 0;
        try(DirectoryStream<Path> direcotryPathStream = Files.newDirectoryStream(directoryPath, "*.{log,txt}")) {
            for(Path path : direcotryPathStream){
                String logFileName = String.valueOf(path.getFileName());
                listOfFiles.add(logFileName);
                fileInputStream = new FileInputStream(path.toFile());
                results = analyzeLogFile(fileInputStream ,filecount, logFileName, inputArchiveFilePath, listOfFiles);
                filecount++;
            }
            logFileAnalyzer.writeToOutputTxtFile((logFileAnalyzer.logType), inputArchiveFilePath, results);
        } catch (IOException e) {
            LogFileAnalyzer.logger.error("IOException while opening directory.",e);
        }
    }

    protected void checkForArchivesInDirectory(String inputPath) {
        Path directoryPath = Paths.get(inputPath);
        try(DirectoryStream<Path> direcotryPathStream = Files.newDirectoryStream(directoryPath, "*.{tar.gz,zip,log,txt}")) {
            for(Path path : direcotryPathStream){
                if((!Files.isDirectory(path)) && (!(path.getFileName().toString().contains("Results"))) && (!Openable.isArchive(path.toString()))){
                    open(inputPath);
                }
                else if(Openable.isArchive(path.toString())){
                    Openable.determineArchiveType(path.toString());
                }
//                else {
//                    Openable.determineArchiveType(path.toString());
//                }
            }
        } catch (IOException e) {
            LogFileAnalyzer.logger.error("IOException while checking directory for archives.",e);
        }
    }
}
