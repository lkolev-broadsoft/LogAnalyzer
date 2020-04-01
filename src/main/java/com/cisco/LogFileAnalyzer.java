package com.cisco;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class LogFileAnalyzer  implements OutputFileWriter{

    protected static final String DATE_REGEX = "(20\\d\\d)\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01])";

    protected static final String TIME_REGEX = "(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]):([0-9]{3})";

    protected static final String TIME_ZONE_REGEX = "(\\w+)";

    protected static final String LOG_LEVEL_REGEX = "(\\bFieldDebug\\b|\\bInfo\\b|\\bDebug\\b|\\bNotice\\b|\\bWarn\\b)";

    protected static final String LOG_TYPE_REGEX = "(\\w+)"; //Change it later to match only the log types

    protected static SortedMap<String, Long> logsPerSecond = new TreeMap<>();

    protected List<String> secondsMissingInLog = new ArrayList<>();

    protected List<String> seconds = new ArrayList<>();

    protected String logType;

    protected static final Logger logger = LoggerFactory.getLogger(LogFileAnalyzer.class);



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
            logger.error("Exception occurred trying to read getTimeFrameList inputStream", e);
            return Collections.emptyList();
        }
    }

    public SortedMap<String, Long> countSeconds(List<String> timeFrame){
        Map<String, Long> inputMap = timeFrame.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        return new TreeMap<>(inputMap); //Added the resulting map to treemap
    }

//    protected void addSecondsToLogPerSecondsMap(List<String> timeFrame){
//        logsPerSecond.appendtimeFrame.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
//    }


    public void writeToOutputTxtFile(String filename, String inputArchiveFilePath, Map<String, Object> inputMap){
        File dir = new File(OutputFileWriter.getFolderPath(inputArchiveFilePath) + File.separator + "Results");
        dir.mkdirs();
        File file = new File(dir, filename);
        try (FileWriter writer = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(writer)) {
            for(Map.Entry<String, Object> entry : inputMap.entrySet()) {
                bw.write((entry.getKey() + " - " + entry.getValue() + "\n"));
            }
        } catch (IOException e) {

            logger.error("IOException: %s%n", e);
        }
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

    protected void addResultsFromFileToMap(List<String> timeFrame){

    }


    //Might not need to get the fileNames as there would be only one file per start.
    //Maybe two if there are IMP and Presence logs logtype +.txt
    //Use logType +.txt instead.
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

    protected void addFileResultsMapToLogsPerSecond(SortedMap<String, Long> fileResultsMap){
        fileResultsMap.forEach(
                (key, value) -> logsPerSecond.merge( key, value,  (v1, v2) -> v1 + v2));
    }

    protected Map analyzeLog(InputStream inputStream, String logFileName){
        seconds = getTimeFrameList(inputStream);
        //logsPerSecond = countSeconds(seconds);
        addFileResultsMapToLogsPerSecond(countSeconds(seconds));
        secondsMissingInLog = findPauses(getTimeFrame(logsPerSecond),logsPerSecond);
        return addPausesToMap(secondsMissingInLog, logsPerSecond);
    }

}
