package com.cisco;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class LogFileAnalyzer {

    protected static final String DATE_REGEX = "(20\\d\\d)\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01])";

    protected static final String TIME_REGEX = "(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]):([0-9]{3})";

    protected static final String TIME_ZONE_REGEX = "(\\w+)";

    protected static final String LOG_LEVEL_REGEX = "(\\bFieldDebug\\b|\\bInfo\\b|\\bDebug\\b|\\bNotice\\b|\\bWarn\\b)";

    protected static final String LOG_TYPE_REGEX = "(\\w+)"; //Change it later to match only the log types

    protected SortedMap<String, Long> logsPerSecond = new TreeMap<>();

    protected List<String> secondsMissingInLog = new ArrayList<>();

    protected List<String> seconds = new ArrayList<>();

    protected String logType;

//    public List<String> getTimeFrameList(InputStream inputStream) {
//        String pattern = DATE_REGEX +"\\s" + TIME_REGEX + "\\s" + TIME_ZONE_REGEX + "\\s\\|\\s" + LOG_LEVEL_REGEX + "\\s+\\|\\s" + LOG_TYPE_REGEX;
//        List<String> timeFrame = new ArrayList<>();
//        Pattern r = Pattern.compile(pattern);
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                Matcher m = r.matcher(line);
//                if(m.find()){
//                    timeFrame.add(m.group(1) + "-" + m.group(2) + "-" + m.group(3) + " " + m.group(4) + ":" + m.group(5) + ":" + m.group(6));
//                }
//            }
//            return timeFrame;
//        } catch (Exception e) {
//            System.err.format("Exception occurred trying to read '%s'.", inputStream);
//            e.printStackTrace();
//            return Collections.emptyList();
//        }
//    }

    public List<String> getTimeFrameList(InputStream inputStream) {
        String pattern = DATE_REGEX +"\\s" + TIME_REGEX + "\\s" + TIME_ZONE_REGEX + "\\s\\|\\s" + LOG_LEVEL_REGEX + "\\s+\\|\\s" + LOG_TYPE_REGEX;
        List<String> timeFrame = new ArrayList<>();
        Pattern r = Pattern.compile(pattern);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = r.matcher(line);
                if(m.find()){
                    timeFrame.add(m.group(1) + "-" + m.group(2) + "-" + m.group(3) + " " + m.group(4) + ":" + m.group(5) + ":" + m.group(6));
                }
            }
            return timeFrame;
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", inputStream);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public SortedMap<String, Long> countSeconds(List<String> timeFrame){
        Map<String, Long> inputMap = timeFrame.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        return new TreeMap<>(inputMap); //Added the resulting map to treemap
    }

    protected List<String> getTimeFrame(SortedMap<String, Long> sortedMap){
        String startTime = sortedMap.firstKey();   // Get String with the starting time
        String endTime = sortedMap.lastKey();        // Get String with the ending time
        String pattern = "(20\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\\s(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])";
        Pattern r = Pattern.compile(pattern);
        Matcher startMatcher = r.matcher(startTime);
        startMatcher.matches();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startMatcher.group(1) + "-" + startMatcher.group(2) + "-" + startMatcher.group(3) + " " + startMatcher.group(4) + ":" + startMatcher.group(5) + ":" + startMatcher.group(6), formatter);
        Matcher endMatcher = r.matcher(endTime);
        endMatcher.matches();
        LocalDateTime end = LocalDateTime.parse(endMatcher.group(1) + "-" + endMatcher.group(2) + "-" + endMatcher.group(3) + " " + endMatcher.group(4) + ":" + endMatcher.group(5) + ":" + endMatcher.group(6), formatter);
        String timePhrase = start.format(formatter);
        List<String> timeFrame = new ArrayList<>();

        while(!start.equals(end)){
            timeFrame.add(timePhrase);
            start = start.plusSeconds(1);  // update startTime
            timePhrase = start.format(formatter); // update timephrase
        }
        return timeFrame;
    }

    public List<String> findPauses(List<String> timeFrame, SortedMap<String, Long> sortedMap){
        List<String > missingSeconds = new ArrayList<>();

        for(String second: timeFrame){
            if(!sortedMap.containsKey(second)){
                missingSeconds.add(second);
            }
        }
        return missingSeconds;
    }

    public SortedMap<String, Long> addPausesToMap(List<String> timeFrame, SortedMap<String, Long> sortedMap){

        for(String second: timeFrame){
            if(!sortedMap.containsKey(second)){
                sortedMap.put(second,  (long) 0);
            }
        }
        return sortedMap;
    }



    protected void writeToOutputTxtFile(String filename, Map<String, Long> inputMap){
        try (FileWriter writer = new FileWriter(filename);
             BufferedWriter bw = new BufferedWriter(writer)) {
            for(Map.Entry<String, Long> entry : inputMap.entrySet()) {
                bw.write((entry.getKey() + " - " + entry.getValue() + "\n"));
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }

    //Overloaded method
    protected void writeToOutputTxtFile(Map<String, String> inputMap, String filename){
        try (FileWriter writer = new FileWriter(filename);
             BufferedWriter bw = new BufferedWriter(writer)) {
            for(Map.Entry<String, String> entry : inputMap.entrySet()) {
                bw.write((entry.getKey() + " - " + entry.getValue() + "\n"));
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }


    protected List<String> getFileNames(List<String> listOfEntries) {
        List<String> filenames = new ArrayList<>();
        String pattern = logType + "(\\d{4}\\.\\d{2}\\.\\d{2})-(\\d{2}\\.\\d{2}\\.\\d{2})(.txt)";
        Pattern r = Pattern.compile(pattern);
        for(String entry : listOfEntries){
            Matcher m = r.matcher(entry);
            if(m.find()){
                filenames.add(m.group());
            }
        }
        return filenames;
    }

    protected Map<String, Long> analyzeLog(InputStream inputStream){
        seconds = getTimeFrameList(inputStream);
        logsPerSecond = countSeconds(seconds);
        secondsMissingInLog = findPauses(getTimeFrame(logsPerSecond),logsPerSecond);
        return addPausesToMap(secondsMissingInLog, logsPerSecond);
    }

}
