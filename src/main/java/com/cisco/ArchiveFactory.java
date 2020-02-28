package com.cisco;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*

D

 */

/**
 *  Determines the type of archive and has a open method to open the archive.
 *
 * @author lkolev
 */
public interface ArchiveFactory {

//    ZIPedLogsFactory zipLogs = new ZIPedLogsFactory();
//    TARGZedLogsFactory tarGZLogs = new TARGZedLogsFactory();
//    DirectoryFactory directoryLogs = new DirectoryFactory();

    Logger logger = LoggerFactory.getLogger(ArchiveFactory.class);

    void open(String inputArchiveFilePath);

//        static void determineArchiveType(String inputArchiveFilePath){
//        String pattern = "(\\.zip)|(\\.tar\\.gz)";
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(inputArchiveFilePath);
//        if(m.find()){
//            if(m.group().equals(m.group(1))){
//                zipLogs.open(inputArchiveFilePath);
//            }
//            else if(m.group().equals(m.group(2))){
//                tarGZLogs.open(inputArchiveFilePath);
//            }
//            else {
//                logger.error("Unsupported input type, only zip and tar.gz  or directories files are accepted.");
//            }
//        }
//        else {
//            directoryLogs.open(inputArchiveFilePath);
//        }
//    }

    static ArchiveFactory determineArchiveType(String inputArchiveFilePath){
        String pattern = "(\\.zip)|(\\.tar\\.gz)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(inputArchiveFilePath);
        if(m.find()){
            if(m.group().equals(m.group(1))){
                return new ZIPedLogsFactory(inputArchiveFilePath);
            }
            else if(m.group().equals(m.group(2))){
                return new TARGZedLogsFactory(inputArchiveFilePath);
            }
            else {
                logger.error("Unsupported input type, only zip and tar.gz  or directories files are accepted.");
            }
        }
        else {
            return new DirectoryFactory(inputArchiveFilePath);
        }
        return null;
    }



}
