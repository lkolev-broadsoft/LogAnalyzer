package com.cisco;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ArchiveFactory {

    ZIPedLogsFactory zipLogs = new ZIPedLogsFactory();
    TARGZedLogsFactory tarGZLogs = new TARGZedLogsFactory();

    void open(String inputArchiveFilePath);

//    default String determineArchiveType(String inputArchiveFile){
//        String pattern = "(\\.zip)|(\\.tar\\.gz)";
//        String archiveType = "DIRECTORY";
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(inputArchiveFile);
//        if(m.find()){
//            if(m.group().equals(m.group(1))){
//                archiveType = "ZIP";
//            }
//            else if(m.group().equals(m.group(2))){
//                archiveType = "TAR.GZ";
//            }
//            else {
//                System.out.println("Unsupported input type, only zip and tar.gz files are accepted.");
//            }
//            archiveType = "DIRECTORY";
//        }
//        return archiveType;
//    }

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
                System.out.println("Unsupported input type, only zip and tar.gz files are accepted.");
            }
        }
    }



}
