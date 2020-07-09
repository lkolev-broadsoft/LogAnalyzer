package com.cisco;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Duration;


public class IMPLogAnalyzer extends LogFileAnalyzer {

    protected static final String CCM_MUC_JOIN_REQUEST = "CCM processSocketData MUC JOIN PRESENCE";
    protected static final String CM_MUC_JOIN_RESPONSE = "CM writePacketToSocket MUC JOIN PRESENCE";

    protected static final String IP_PORT_REGEX = "(\\d+.\\d+.\\d+.\\d+_(\\d+))";

    protected static final String DOMAIN_NAME = "(\\w+-\\w+-\\w+-\\w+)";

    protected static final String MUC_REQUEST_NICKNAME_PARSER = "DATA=<presence to=(\\\"\\w+@\\w+.broadsoftdefault.com)\\/(?<nickName>\\w+)\\\"";

    protected static final String MUC_RESPONSE_NICKNAME_PARSER = "<x xmlns=\"http://jabber.org/protocol/muc#user\"><status code=\"110\"/><item affiliation=\"none\" role=\"participant\" nick=\"(?<nickName>\\w+)\"";

    protected static final String P_MUC_JOIN_PRESENCE_REQUEST_STRING = "\\[p=from=c2s@" + DOMAIN_NAME + "/" + IP_PORT_REGEX + "_" + IP_PORT_REGEX + ", to=sess-man@" + DOMAIN_NAME + ", " + MUC_REQUEST_NICKNAME_PARSER;

    //protected static final String P_MUC_JOIN_PRESENCE__RESPONSE_STRING = "\\[p=from=c2s@" + DOMAIN_NAME + "/" + IP_PORT_REGEX + "_" + IP_PORT_REGEX + ", to=sess-man@" + DOMAIN_NAME + ", " + MUC_JOIN_DATA;


    protected static final String THE_STRING = "(20\\d\\d)\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01])\\s(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]):([0-9]{3})\\s(\\w+)\\s\\|\\s(\\bFieldDebug\\b|\\bInfo\\b|\\bDebug\\b|\\bNotice\\b|\\bWarn\\b)\\s+\\|\\s(\\w+)\\R\\R\\sCCM processSocketData MUC JOIN PRESENCE \\[p=from=c2s@(\\w+-\\w+-\\w+-\\w+)\\/(?<ip>\\d+.\\d+.\\d+.\\d+_(\\d+))_(?<ip2>\\d+.\\d+.\\d+.\\d+_(\\d+)), to=sess-man@(\\w+-\\w+-\\w+-\\w+), (DATA=<presence to=(?<roomName>\\\"\\w+@\\w+.broadsoftdefault.com)/(?<nickName>\\w+)\\\")";


    protected static SortedMap<LocalDateTime, List<String>> oldjoinRoomRequestMap = new TreeMap<>();

    protected static SortedMap<String, LocalDateTime> joinRoomRequestMap = new TreeMap<>();
    protected static SortedMap<String,LocalDateTime> joinRoomResponseMap = new TreeMap<>();

    protected static SortedMap<String,String> mucJoinResponseTime = new TreeMap<>();

    protected static Map<String, String> impStatisticsMap = new HashMap<>();


    public IMPLogAnalyzer() {
        this.logType = "IMPLog";
    }

//    public void getJoinRoomRequestTimeMap(InputStream inputStream) {
////        String pattern = DATE_REGEX +"\\s" + TIME_REGEX + "\\s" + TIME_ZONE_REGEX + "\\s\\|\\s" + LOG_LEVEL_REGEX + "\\s+\\|\\s" + LOG_TYPE_REGEX + CCM_BOBY_MAGIC_STRING;
//        String pattern = THE_STRING;
//
//        Pattern r = Pattern.compile(pattern);
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                Matcher m = r.matcher(line);
//                if(m.find()){
//                    String nickName = m.group("nickName");
//                    joinRoomRequestMap.put(nickName, (m.group(1) + "-" + m.group(2) + "-" + m.group(3) + " " + m.group(4) + ":" + m.group(5) + ":" + m.group(6) + ":" + m.group(7)));
//                    System.out.println(nickName + "/" + (m.group(1) + "-" + m.group(2) + "-" + m.group(3) + " " + m.group(4) + ":" + m.group(5) + ":" + m.group(6) + ":" + m.group(7)));
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Exception occurred trying to read getTimeFrameList inputStream", e);
//        }
//    }

    public void getJoinRequestAndResponseTimeStampMap(InputStream inputStream) {
        String pattern = DATE_REGEX + "\\s" + TIME_REGEX + "\\s" + TIME_ZONE_REGEX + "\\s\\|\\s" + LOG_LEVEL_REGEX + "\\s+\\|\\s" + LOG_TYPE_REGEX;
        LocalDateTime timeStamp = null;
        Pattern r = Pattern.compile(pattern);
        Pattern mucRequestPattern = Pattern.compile(MUC_REQUEST_NICKNAME_PARSER);
        Pattern mucResponsePattern = Pattern.compile(MUC_RESPONSE_NICKNAME_PARSER);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = r.matcher(line);
                if (m.find()) {
                    timeStamp = LocalDateTime.parse(m.group(1) + "-" + m.group(2) + "-" + m.group(3) + " " + m.group(4) + ":" + m.group(5) + ":" + m.group(6) + ":" + m.group(7), formatter);
                }
                Matcher mucRequestMatcher = mucRequestPattern.matcher(line);
                String nickName = null;
                if(mucRequestMatcher.find()){
                    nickName = mucRequestMatcher.group("nickName");
                    joinRoomRequestMap.put(nickName, timeStamp);
                }
                Matcher mucResponseMatcher = mucResponsePattern.matcher(line);
                String responseNickName = null;
                if(mucResponseMatcher.find()){
                    responseNickName = mucResponseMatcher.group("nickName");
                    joinRoomResponseMap.put(responseNickName, timeStamp);
                }
//                if (line.contains(CM_MUC_JOIN_RESPONSE)) {
//                    joinRoomResponseMap.put(nickName, timeStamp);
//
//                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred trying to read getTimeStamp from inputStream", e);
        }
    }


//    protected LocalDateTime getTimeStamp(SortedMap<String, Long> sortedMap){
//        String startTime = sortedMap.firstKey();   // Get String with the starting time
//        String endTime = sortedMap.lastKey();        // Get String with the ending time
//        String pattern = "(20\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\\s(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])";
//        Pattern r = Pattern.compile(pattern);
//        Matcher startMatcher = r.matcher(startTime);
//        startMatcher.matches();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime start = LocalDateTime.parse(startMatcher.group(1) + "-" + startMatcher.group(2) + "-" + startMatcher.group(3) + " " + startMatcher.group(4) + ":" + startMatcher.group(5) + ":" + startMatcher.group(6), formatter);
//        Matcher endMatcher = r.matcher(endTime);
//        endMatcher.matches();
//        LocalDateTime end = LocalDateTime.parse(endMatcher.group(1) + "-" + endMatcher.group(2) + "-" + endMatcher.group(3) + " " + endMatcher.group(4) + ":" + endMatcher.group(5) + ":" + endMatcher.group(6), formatter);
//        String timePhrase = start.format(formatter);
//        List<String> timeFrame = new ArrayList<>();
//
//        while(!start.equals(end)){
//            timeFrame.add(timePhrase);
//            start = start.plusSeconds(1);  // update startTime
//            timePhrase = start.format(formatter); // update timephrase
//        }
//        return timeFrame;
//    }

    protected Map<String, String> calculateMUCJoinRoomResponseTime(SortedMap<LocalDateTime, List<String>> sortedMap) {
        String mucPattern = MUC_REQUEST_NICKNAME_PARSER;
        Pattern r = Pattern.compile(mucPattern);
        String nickName = "";
        Map<String, String> responseTimeForMucJoin = new HashMap<>();
//            sortedMap.forEach((key, valueList) -> valueList.forEach(listItem -> System.out.println(key + " " + listItem)));
        for (Map.Entry<LocalDateTime, List<String>> entry : sortedMap.entrySet()) {
            LocalDateTime timeStamp = entry.getKey();
            for (String value : entry.getValue()) {
//                System.out.println(value);
                Matcher m = r.matcher(value);
                if (m.find()) {
                    nickName = m.group("nickName");
//                    System.out.println(timeStamp + nickName);
//                    if(value.contains(nickName)){
//                        responseTimeForMucJoin.put(timeStamp.toString(),nickName);
//                        System.out.println(timeStamp + " : " + nickName);
                }
                else{
                    if(value.contains(nickName)){
                        responseTimeForMucJoin.put(timeStamp.toString(),nickName);
                        System.out.println(timeStamp + " : " + nickName);
                    }
                }

            }

        }
        return responseTimeForMucJoin;
    }

    protected void addRequestResponseTimeToResults(){
        for(Map.Entry<String, LocalDateTime> responseEntry : joinRoomResponseMap.entrySet()){
            System.out.println();
        }
        for(Map.Entry<String, LocalDateTime> entry : joinRoomRequestMap.entrySet()){
            LocalDateTime requestLocalDateTime = entry.getValue();
//            System.out.println(joinRoomResponseMap.get(entry.getKey()));
            LocalDateTime responseLocalDateTime = joinRoomResponseMap.get(entry.getKey());
            String responseTimeString;
            if(responseLocalDateTime != null ){
                Duration duration = Duration.between(requestLocalDateTime, responseLocalDateTime);
                responseTimeString = duration.getSeconds() + " seconds " + TimeUnit.NANOSECONDS.toMillis(duration.getNano()) + " milliseconds\n";
            }
            else{
//                Duration duration = Duration.between(joinRoomResponseMap.get(entry.getKey()),entry.getValue());
                responseTimeString = "No Response found.\n";
            }
            mucJoinResponseTime.put(entry.getKey(), responseTimeString );
        }
    }




    protected Map analyzeLog(InputStream inputStream, String logFileName){
//        seconds = getTimeFrameList(inputStream);
//        // Was Commented //logsPerSecond = countSeconds(seconds);
//        addFileResultsMapToLogsPerSecond(countSeconds(seconds));
//        secondsMissingInLog = findPauses(getTimeFrame(logsPerSecond),logsPerSecond);
//        resultsMap = addPausesToMap(secondsMissingInLog, logsPerSecond);
        getJoinRequestAndResponseTimeStampMap(inputStream);
        Map<String, String> responseTimeForMucJoin = calculateMUCJoinRoomResponseTime(oldjoinRoomRequestMap);
        addRequestResponseTimeToResults();
       // resultsMap.putAll(joinRoomRequestMap);
//        return responseTimeForMucJoin;
        return mucJoinResponseTime;
    }




    /*Add two maps in new class ImpStatisticsData
    One map holding LogsPerSecond, where second is the key and the value is the number of logs
    Another map holding the MUCJoinRoomResponseTime, where the nickname is the key and the value is the time it took
    to receive the response.
    */

    public void writeToOutputTxtFile(String filename, String inputArchiveFilePath, Map<String, Object> inputMap){
        File dir = new File(OutputFileWriter.getFolderPath(inputArchiveFilePath) + File.separator + "Results");
        dir.mkdirs();
        File file = new File(dir, filename + "LogsPerSecond" + ".txt");
        try (FileWriter writer = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(writer)) {
            for(Map.Entry<String, Object> entry : inputMap.entrySet()) {
                bw.write((entry.getKey() + " - " + entry.getValue() + "\n"));
            }
        } catch (IOException e) {

            logger.error("IOException: %s%n", e);
        }
    }



}
