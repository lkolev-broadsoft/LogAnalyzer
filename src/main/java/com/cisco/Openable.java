package com.cisco;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Determines the type of archive and has a open method to open the archive.
 *
 * @author lkolev
 */

//Change interface name
public interface Openable {

    void open(String inputArchiveFilePath);

    //extract to abstract class
    static Openable determineArchiveType(String inputArchiveFilePath){
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
                LogFileAnalyzer.logger.error("Unsupported input type, only zip and tar.gz  or directories files are accepted.");
            }
        }
        else {
            return new DirectoryFactory(inputArchiveFilePath);
        }
        return null;
    }



}