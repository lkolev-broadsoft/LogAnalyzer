package com.cisco;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatsLogAnalyzer  extends  LogFileAnalyzer implements OutputFileWriter{

    protected static final String MESSAGE_ROUTER = "message-router";

    protected static final String AMP = "amp";

    protected static final String BOSH = "bosh";

    protected static final String C2S = "c2s";

    protected static final String CL_COMP = "cl_comp";

    protected static final String CONTACTS_STORAGE_COMPONENT = "contactstoragecomp";

    protected static final String CPR_SERVICE = "cprService";

    protected static final String DELETE_ENTERPRISE_COMPONENT = "deleteEnterpriseComponent";

    protected static final String EXT = "ext";

    protected static final String INCALL_COMPONENT = "incallcomp";

    protected static final String MOBILE_AWAY_COMPONENT = "mobileAwayComponent";

    protected static final String MONITOR = "monitor";

    protected static final String PROVISIONING_ADAPTER_COMPONENT = "provisioningAdapterComponent";

    protected static final String PROXY = "proxy";

    protected static final String PUBSUB = "pubsub";

    protected static final String READ_MSG_COMPONENT = "readmsgcomp";

    protected static final String ROOM_COMPONENT = "roomcomponent";

    protected static final String S2S = "s2s";

    protected static final String SEND_MESSAGE_COMPONENT = "sendmessagecomp";

    protected static final String S_RECEIVE = "srecv";

    protected static final String S_SEND = "ssend";

    protected static final String SUPER_PRESENCE_COMPONENT = "superPresenceComponent";

    protected static final String XMPP_VERIFICATION_COMPONENT = "xmppVerificationComponent";

    protected static final String SESS_MAN = "sess-man";

    protected static final String SERVICE = "(?<serviceName>\\bmessage-router\\b|\\bc2s\\b|\\bs2s\\b|\\bsess-man\\b)";

    protected static final String STATS_DATE_REGEX = "(?<year>20\\d\\d)-(?<month>0[1-9]|1[012])-(?<day>0[1-9]|[12][0-9]|3[01])";

    protected static final String STATS_TIME_REGEX = "(?<hour>0[0-9]|1[0-9]|2[0-3])[:|_|/](?<minute>[0-5][0-9])[:|_|/](?<second>[0-5][0-9])";

    protected SortedMap<String, String> statValuesMap = new TreeMap<>();

    //protected SortedMap<String, String> results = new TreeMap<>();

    protected StatisticData[] statisticDataArray;

    protected StatisticData statisticData;


    public StatsLogAnalyzer(){
        this.logType = "stats";
    }
    
    protected List<String> getStatsValues(InputStream inputStream){
        List<String> statValues = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); // Not closing due to NullPonterException in gzipCompressorInputStream
            String line;
            while ((line = reader.readLine()) != null) {
                    statValues.add(line);
                }
            } catch (IOException e) {
            logger.error("IOException while reading inputStream from stats logs", e);
        }
        return statValues;
    }

    protected void getLastPackets(List<String> inputList){
        String pattern = SERVICE + "/(?<lastPackets>Last (?<timeRange>\\bminute\\b|\\bsecond\\b|\\bhour\\b) packets)\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statValuesMap.put(m.group("serviceName") + "/" + m.group("lastPackets") ,(m.group("packets")));
            }
        }
    }

//    protected ArrayList<String> getLastPacketsAsList(List<String> inputList){
//        String pattern = SERVICE + "/(?<lastPackets>Last (?<timeRange>\\bminute\\b|\\bsecond\\b|\\bhour\\b) packets)\\s+(?<packets>\\d+)";
//        Pattern r = Pattern.compile(pattern);
//        ArrayList<String> results;
//        for(String line : inputList){
//            Matcher m = r.matcher(line);
//            if(m.find()){
//                results.add(m.group("serviceName") + "/" + m.group("lastPackets") ,(m.group("packets")));
//            }
//        }
//        return results;
//    }

    protected void getOverflows(List<String> inputList){
        String pattern = SERVICE + "/((?<overflowType>\\bSocket\\b|\\bIN Queue\\b|\\bOUT Queue\\b|\\bTotal queues\\b) overflow)\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.matches()){
                statValuesMap.put(m.group("serviceName") + "/" + m.group("overflowType") + " overflow" ,(m.group("packets")));
            }
        }
    }

    protected void getSessManProcessor(List<String> inputList){
        String pattern = SERVICE + "(/Processor): (.*),(.*)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statValuesMap.put(m.group("serviceName") + m.group(2) + " " + (m.group(3)), m.group(4));
            }
        }
    }

    protected void getCPUusage(List<String> inputList){
        String pattern = SERVICE + "/(?<usageType>(\\bCPU\\b|\\bHEAP\\b|\\bNONHEAP\\b) usage) (.*)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statValuesMap.put(m.group("serviceName") + "/" + m.group("usageType"), m.group(4));
            }
        }
    }

//    protected void extractTimeFromFilename(List<String> listOfFiles){
//        String pattern = STATS_DATE_REGEX + "_" + STATS_TIME_REGEX;
//        Pattern r = Pattern.compile(pattern);
//        for(String line : listOfFiles){
//            Matcher m = r.matcher(line);
//            if(m.matches()){
//                statValuesMap.put(m.group("serviceName") + "/" + m.group("usageType"), m.group(4));
//            }
//        }
//    }

//    protected List<String> extractTimeFromFilename(String fileName){
//        String pattern = STATS_DATE_REGEX + "_" + STATS_TIME_REGEX;
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(fileName);
//        List<String> time = new ArrayList<>();
//        if(m.matches()){
//            time.add(m.group("year") + "-" + m.group("month") + "-" + m.group("day") + " "
//                            + m.group("hour") + ":" + m.group("minute") + ":" + m.group("second"));
//        }
//        return time;
//    }

//        protected String extractTimeStringFromFilename(String fileName){
//        String pattern = STATS_DATE_REGEX + "_" + STATS_TIME_REGEX;
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(fileName);
//        String time = "Couldn't extract Time";
//        if(m.matches()){
//            time = (m.group("year") + "-" + m.group("month") + "-" + m.group("day") + " "
//                    + m.group("hour") + ":" + m.group("minute") + ":" + m.group("second"));
//        }
//        return time;
//    }

    /*
    Create a StatisticDate object and add the time from the fileName as a time in StatisticData object.
     */
//    protected StatisticData extractLocalDateTimeFromFilename(String fileName){
//        StatisticData statisticDataTime = new StatisticData();
//        String pattern = STATS_DATE_REGEX + "_" + STATS_TIME_REGEX;
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(fileName);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime localDateTime = null;
//        if(m.find()){
//            localDateTime = LocalDateTime.parse(m.group("year") + "-" + m.group("month") + "-" + m.group("day") + " " + m.group("hour") + ":" + m.group("minute") + ":" + m.group("second"), formatter);
//        }
//        statisticDataTime.setTime(localDateTime);
//        return statisticDataTime;
//    }


    //Extract LocalDateTime from fileName (One responsibility, only)

    protected LocalDateTime extractLocalDateTimeFromFilename(String fileName){
        StatisticData statisticDataTime = new StatisticData();
        String pattern = STATS_DATE_REGEX + "_" + STATS_TIME_REGEX;
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(fileName);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = null;
        if(m.find()){
            localDateTime = LocalDateTime.parse(m.group("year") + "-" + m.group("month") + "-" + m.group("day") + " " + m.group("hour") + ":" + m.group("minute") + ":" + m.group("second"), formatter);
        }
        statisticDataTime.setTime(localDateTime);
        return localDateTime;
    }

//    protected Map<String,Long> getStatisticValueAtTime(String fileName , List<String> statsList){
//        SortedMap<String, String> statisticValueAtTime = new TreeMap<>();
//        String time = extractTimeStringFromFilename(fileName);
//        Long value = getLastPackets(statsList);
//
//    }

    protected List<String> getFileNames(List<String> listOfEntries) {
        List<String> filenames = new ArrayList<>();
        String pattern = logType + "_" + "(\\d{4}-\\d{2}-\\d{2})_(\\d{2}_\\d{2}_\\d{2})(.txt)";
        Pattern r = Pattern.compile(pattern);
        for(String entry : listOfEntries){
            Matcher m = r.matcher(entry);
            if(m.find()){
                filenames.add(m.group());
            }
        }
        return filenames;
    }

    public void writeToOutputTxtFile(String filename, Map<String, Object> inputMap){
        try (FileWriter writer = new FileWriter(filename);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            for(Map.Entry<String, Object> entry : inputMap.entrySet()) {
                bufferedWriter.write((entry.getKey() + " - " + entry.getValue() + "\n"));
            }
        } catch (IOException e) {
            logger.error("IOException while writing to Output text file.", e);
        }
    }

    protected Map<String, String> analyzeLog(InputStream inputStream, String logFileName){
        List<String> statsList = getStatsValues(inputStream);
        getLastPackets(statsList);
        getOverflows(statsList);
        getSessManProcessor(statsList);
        getCPUusage(statsList);
        return statValuesMap;
    }

}
