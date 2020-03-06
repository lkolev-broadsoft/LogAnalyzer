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

//    protected StatisticData[] statisticDataArray;

    protected static ArrayList<StatisticData> statisticDataArrayList = new ArrayList<>();

    protected StatisticData statisticData;

    public StatsLogAnalyzer(){
        this.logType = "stats";
    }

//    public void writeToOutputTxtFile(String filename, Map<String, Object> inputMap){
//        try (FileWriter writer = new FileWriter(filename + ".txt");
//             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
//            for(Map.Entry<String, Object> entry : inputMap.entrySet()) {
//                bufferedWriter.write((entry.getKey() + " - " + entry.getValue() + "\n"));
//            }
//        } catch (IOException e) {
//            logger.error("IOException while writing to Output text file.", e);
//        }
//    }

//    public void writeToOutputTxtFile(String filename, Map<String, Object> inputMap, Boolean isLastFileInArchive){
//        if(isLastFileInArchive){
//            try (FileWriter writer = new FileWriter( "c2sLastHourPackets" + ".txt");
//                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
//                for(Map.Entry<String, Object> entry : inputMap.entrySet()) {
//                    String key = entry.getKey();
//                    StatisticData statisticDataObject = (StatisticData) entry.getValue();
//                    Long value = statisticDataObject.getValue();
//                    bufferedWriter.write((entry.getKey() + "  " + value + "\n"));
//                }
//            } catch (IOException e) {
//                logger.error("IOException while writing to Output text file.", e);
//            }
//        }
//        else{
//            //statisticDataArrayList.add(statisticData);
//            System.out.println("NotLastFile");
//        }
//
//    }

    public void writeToOutputTxtFile(String filename, Map<String, Object> inputMap, Boolean isLastFileInArchive){
        try (FileWriter writer = new FileWriter( "c2sLastHourPackets" + ".txt");
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            for(Map.Entry<String, Object> entry : inputMap.entrySet()) {
                String key = entry.getKey();
                StatisticData statisticDataObject = (StatisticData) entry.getValue();
                Long value = statisticDataObject.getValue();
                bufferedWriter.write((entry.getKey() + "  " + value + "\n"));
            }
        } catch (IOException e) {
            logger.error("IOException while writing to Output text file.", e);
        }
    }

    protected Map<String, Object> createResultsMapFromStatisticDataObject(List<StatisticData> statisticDataList){
        SortedMap<String, Object> resultsMap = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for(StatisticData statisticData : statisticDataList){
            resultsMap.put(statisticData.getTime().format(formatter),statisticData);
        }
        return resultsMap;
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

    // Use to add statisticName and value to a StatisticData object

    protected SortedMap<String,Long> getLastPacketsAsMap(List<String> inputList){
        String pattern = SERVICE + "/(?<lastPackets>Last (?<timeRange>\\bminute\\b|\\bsecond\\b|\\bhour\\b) packets)\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        SortedMap<String,Long> results = new TreeMap<>();
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                results.put(m.group("serviceName") + "/" + m.group("lastPackets") ,(Long.valueOf(m.group("packets"))));
            }
        }
        return results;
    }

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

    protected SortedMap<String,Long> getOverflowsAsMap(List<String> inputList){
        String pattern = SERVICE + "/((?<overflowType>\\bSocket\\b|\\bIN Queue\\b|\\bOUT Queue\\b|\\bTotal queues\\b) overflow)\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        SortedMap<String,Long> results = new TreeMap<>();
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.matches()){
                results.put(m.group("serviceName") + "/" + m.group("overflowType") + " overflow" ,(Long.valueOf(m.group("packets"))));
            }
        }
        return results;
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

    //Extract LocalDateTime from fileName (One responsibility, only)

    protected LocalDateTime extractLocalDateTimeFromFilename(String fileName){
        StatisticData statisticDataTime = new StatisticDataBuilder().createStatisticData();
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
        String pattern = logType + "_" + STATS_DATE_REGEX + "_" + STATS_TIME_REGEX;
        Pattern r = Pattern.compile(pattern);
        for(String entry : listOfEntries){
            Matcher m = r.matcher(entry);
            if(m.find()){
                filenames.add(m.group());
            }
        }
        return filenames;
    }


    /**
     * 2020-02-03 14:12:22 243453(value) !ifvalue exceeds the value limit)boolean flag
     */

//    public void writeToOutputTxtFile(String filename, List<StatisticData> inputArrayList){
//        try (FileWriter writer = new FileWriter(filename);
//             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
//            for(StatisticData statisticData : inputArrayList) {
//                bufferedWriter.write((statisticData + "\n"));
//            }
//        } catch (IOException e) {
//            logger.error("IOException while writing to Output text file.", e);
//        }
//    }

    //Something wrong with the builder(fix later)
//    protected StatisticData createStatisticDataObject(Map inputMap, LocalDateTime time){
//        return new StatisticDataBuilder().setTime(time).setServerStatistic("c2s/Last hour packets").setValue((Long)inputMap.get("c2s/Last hour packets")).createStatisticData();
//    }

    protected StatisticData createStatisticDataObject(Map inputMap, LocalDateTime time){
        StatisticData statisticDataObject = new StatisticData(time,"c2s/Last hour packets", (Long) inputMap.get("c2s/Last hour packets"));
        return statisticDataObject;
    }


    @Override
    protected Map<String, Object> analyzeLog(InputStream inputStream, String logFileName){
        List<String> statsList = getStatsValues(inputStream);
        getLastPackets(statsList);
        //getOverflows(statsList);
        //getSessManProcessor(statsList);
        //getCPUusage(statsList);
        SortedMap<String,Long> testMap = getLastPacketsAsMap(statsList);
        statisticData = createStatisticDataObject(testMap,extractLocalDateTimeFromFilename(logFileName));
        statisticDataArrayList.add(statisticData);
        Map<String, Object> resultsMapFromStatisticDataObject = createResultsMapFromStatisticDataObject(statisticDataArrayList);
        System.out.println(statisticDataArrayList);
//        return statValuesMap;
        return resultsMapFromStatisticDataObject;
    }
    
}
