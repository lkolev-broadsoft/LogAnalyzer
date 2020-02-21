package com.cisco;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ArchiveFactory {

    void open(String inputArchiveFilePath);

    default String determineArchiveType(String inputArchiveFile){
        String pattern = "(\\.zip)|(\\.tar\\.gz)";
        String archiveType = "DIRECTORY";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(inputArchiveFile);
        if(m.find()){
            if(m.group().equals(m.group(1))){
                archiveType = "ZIP";
            }
            else if(m.group().equals(m.group(2))){
                archiveType = "TAR.GZ";
            }
            else {
                System.out.println("Unsupported input type, only zip and tar.gz files are accepted.");
            }
            archiveType = "DIRECTORY";
        }
        return archiveType;
    }
}
