package com.cisco;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ArchiveFactory {

    ZIPedLogsFactory zipLogs = new ZIPedLogsFactory();
    TARGZedLogsFactory tarGZLogs = new TARGZedLogsFactory();

    Logger logger = LoggerFactory.getLogger(ArchiveFactory.class);

    void open(String inputArchiveFilePath);

        static void determineArchiveType(String inputArchiveFilePath){
        String pattern = "(\\.zip)|(\\.tar\\.gz)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(inputArchiveFilePath);
        if(m.find()){
            if(m.group().equals(m.group(1))){
                zipLogs.open(inputArchiveFilePath);
            }
            else if(m.group().equals(m.group(2))){
                tarGZLogs.open(inputArchiveFilePath);
            }
            else {
//                System.out.println("Unsupported input type, only zip and tar.gz files are accepted.");
            logger.error("Unsupported input type, only zip and tar.gz files are accepted.");
            }
        }
    }



}
