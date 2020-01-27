package com.cisco;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IMPLogAnalyzer extends LogFileAnalyzer {

    List<String> filenames = new ArrayList<>();

    @Override
    protected void getFileNames(List <String> listOfEntries) {
        String pattern = "(IMPLog)(\\d{4}\\.\\d{2}\\.\\d{2})-(\\d{2}\\.\\d{2}\\.\\d{2})(.txt)";
        Pattern r = Pattern.compile(pattern);
        for(String entry : listOfEntries){
            Matcher m = r.matcher(entry);
            if(m.find()){
                filenames.add(m.group());
            }
        }
    }
}
