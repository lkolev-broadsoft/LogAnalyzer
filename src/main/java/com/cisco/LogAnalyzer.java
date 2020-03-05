package com.cisco;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LogAnalyzer {

    protected String inputArchiveFilePath;

    protected Path archivePath;

    public LogAnalyzer(String inputArchiveFilePath){
        this.inputArchiveFilePath = inputArchiveFilePath;
        this.archivePath = Paths.get(inputArchiveFilePath);
        Openable.determineArchiveType(inputArchiveFilePath);
    }
}