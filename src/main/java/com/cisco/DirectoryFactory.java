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
        open(inputPath);
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
}
