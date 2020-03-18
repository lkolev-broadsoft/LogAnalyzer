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

    protected static final String SERVICE = "(?<serviceName>\\bmessage-router\\b|\\bc2s\\b|\\bs2s\\b|\\bsess-man\\b|\\bbosh\\b|\\bcl-comp\\b|\\bext\\b)";

    protected static final String STATS_DATE_REGEX = "(?<year>20\\d\\d)-(?<month>0[1-9]|1[012])-(?<day>0[1-9]|[12][0-9]|3[01])";

    protected static final String STATS_TIME_REGEX = "(?<hour>0[0-9]|1[0-9]|2[0-3])[:|_|/](?<minute>[0-5][0-9])[:|_|/](?<second>[0-5][0-9])";

    protected static final String SESSMAN_PROCESSOR_REGEX = "(/Processor): (?<parameter>[A-Za-z0-9/._:-]+)\\s+, (?<value>(?<queue>Queue:) (?<queueValue>\\d+), (?<averageTime>AvTime: )(?<averageTimeValue>\\d+), (?<runs>Runs: )(?<runsValue>\\d+), (?<lost>Lost: )(?<lostValue>\\d+))";

    //protected SortedMap<String, String> statValuesMap = new TreeMap<>();

    protected static Map<String, String> statisticsMap = new HashMap<>();

    protected static ArrayList<StatisticData> statisticDataArrayList = new ArrayList<>();

    private static Set<String> serverStatisticsNames = new HashSet<>();

    private static final String SERVICE_NAME_STRING = "serviceName";

    public StatsLogAnalyzer(){
        this.logType = "stats";
    }


    //Don't write for every file, but once for the archive or the folder
    //Need to check if it is the last entry(file and then write, before that store the data in StatisticDataArrayList
    public void writeToOutputTxtFile(String filename, Map<String, Object> inputMap){
        for(String serverStatisticName : serverStatisticsNames){
            try (FileWriter writer = new FileWriter( serverStatisticName.replace("/","_") + ".txt");
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                for(Map.Entry<String, Object> entry : inputMap.entrySet()) {
                    StatisticData statisticDataObject = (StatisticData) entry.getValue();
                    if(serverStatisticName.replace("_","/").matches(statisticDataObject.getServerStatisticName().replace("_","/"))){
                        String value = statisticDataObject.getValue();
                        bufferedWriter.write((entry.getKey() + "  " + value + "\n"));
                    }
                }
            } catch (IOException e) {
                logger.error("IOException while writing to Output text file.", e);
            }
        }
    }

    //Adding Statistic name to Map's key in order to differentiate
    protected SortedMap<String, Object> createResultsMapFromStatisticDataObject(List<StatisticData> statisticDataList){
        SortedMap<String, Object> resultsMap = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for(StatisticData statisticData : statisticDataList){
            resultsMap.put(statisticData.getTime().format(formatter) + " " + statisticData.getServerStatisticName(),statisticData);
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

//    protected void getLastPackets(List<String> inputList){
//        String pattern = SERVICE + "/(?<lastPackets>Last (?<timeRange>\\bminute\\b|\\bsecond\\b|\\bhour\\b) packets)\\s+(?<packets>\\d+)";
//        Pattern r = Pattern.compile(pattern);
//        for(String line : inputList){
//            Matcher m = r.matcher(line);
//            if(m.find()){
//                statValuesMap.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("lastPackets") ,(m.group("packets")));
//            }
//        }
//    }

//    // Use to add statisticName and value to a StatisticData object
//
//    protected SortedMap<String,Long> getLastPacketsAsMap(List<String> inputList){
//        String pattern = SERVICE + "/(?<lastPackets>Last (?<timeRange>\\bminute\\b|\\bsecond\\b|\\bhour\\b) packets)\\s+(?<packets>\\d+)";
//        Pattern r = Pattern.compile(pattern);
//        SortedMap<String,Long> results = new TreeMap<>();
//        for(String line : inputList){
//            Matcher m = r.matcher(line);
//            if(m.find()){
//                results.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("lastPackets") ,(Long.valueOf(m.group("packets"))));
//            }
//        }
//        return results;
//    }

//    protected void getOverflows(List<String> inputList){
//        String pattern = SERVICE + "/((?<overflowType>\\bSocket\\b|\\bIN Queue\\b|\\bOUT Queue\\b|\\bTotal queues\\b) overflow)\\s+(?<packets>\\d+)";
//        Pattern r = Pattern.compile(pattern);
//        for(String line : inputList){
//            Matcher m = r.matcher(line);
//            if(m.matches()){
//                statValuesMap.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("overflowType") + " overflow" ,(m.group("packets")));
//            }
//        }
//    }

//    protected SortedMap<String,Long> getOverflowsAsMap(List<String> inputList){
//        String pattern = SERVICE + "/((?<overflowType>\\bSocket\\b|\\bIN Queue\\b|\\bOUT Queue\\b|\\bTotal queues\\b) overflow)\\s+(?<packets>\\d+)";
//        Pattern r = Pattern.compile(pattern);
//        SortedMap<String,Long> results = new TreeMap<>();
//        for(String line : inputList){
//            Matcher m = r.matcher(line);
//            if(m.matches()){
//                results.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("overflowType") + " overflow" ,(Long.valueOf(m.group("packets"))));
//            }
//        }
//        return results;
//    }

//    protected void getSessManProcessor(List<String> inputList){
//        String pattern = SERVICE + "(/Processor): (.*),(.*)";
//        Pattern r = Pattern.compile(pattern);
//        for(String line : inputList){
//            Matcher m = r.matcher(line);
//            if(m.find()){
//                statValuesMap.put(m.group(SERVICE_NAME_STRING) + m.group(2) + " " + (m.group(3)), m.group(4));
//            }
//        }
//    }

//    protected void getCPUusage(List<String> inputList){
//        String pattern = SERVICE + "/(?<usageType>(\\bCPU\\b|\\bHEAP\\b|\\bNONHEAP\\b) usage) (.*)";
//        Pattern r = Pattern.compile(pattern);
//        for(String line : inputList){
//            Matcher m = r.matcher(line);
//            if(m.find()){
//                statValuesMap.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("usageType"), m.group(4));
//            }
//        }
//    }

    protected void getStatisticsFromList(List<String> inputList){
        getLastHourPackets(inputList);
        getCPUandMemory(inputList);
        getTotalOverflows(inputList);
        getSessManRegisteredAccounts(inputList);
        getSessManConnections(inputList);
        getSessManSessions(inputList);
        getHighSessManProcessor(inputList);
    }

    private void getCPUandMemory(List<String> inputList) {
        String pattern = SERVICE + "/(?<usageType>(\\bCPU\\b|\\bHEAP\\b|\\bNONHEAP\\b) usage \\[%])\\s+(?<percentage>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statisticsMap.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("usageType"),(m.group("percentage")));
            }
        }
    }

    private void getLastHourPackets(List<String> inputList) {
        String pattern = SERVICE + "/(?<lastPackets>Last (?<timeRange>\\bhour\\b) packets)\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statisticsMap.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("lastPackets") , m.group("packets"));
            }
        }
    }

    private void getServiceComponentsTotalOverflows(List<String> inputList){
        String pattern = SERVICE + "/((?<overflowType>\\bTotal queues\\b) overflow)\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.matches()){
                String serviceComponentOverflowValue = m.group("packets");
                statisticsMap.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("overflowType") + " overflow" , m.group("packets"));
                if(Long.parseLong(serviceComponentOverflowValue) > 0){
                    getServiceComponentQueues(inputList, m.group(SERVICE_NAME_STRING));
                }
            }
        }
    }

    private void getTotalOverflows(List<String> inputList){
        String pattern = "((total/\\bTotal queues\\b) overflow)\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.matches()){
                String totalOverflowValue = m.group("packets");
                statisticsMap.put(m.group(1),totalOverflowValue);
                if(Long.parseLong(totalOverflowValue) > 0){
                    getServiceComponentsTotalOverflows(inputList);
                }
            }
        }
    }

    private void getSessManRegisteredAccounts(List<String> inputList){
        String pattern = SERVICE + "(/Registered accounts)\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statisticsMap.put(m.group(SERVICE_NAME_STRING) + "/" + m.group(2), m.group("packets"));
            }
        }
    }

    private void getSessManConnections(List<String> inputList){
        String pattern = SERVICE + "/(?<connections>((\\bOpen\\b|\\bMaximum\\b|\\bTotal\\b|\\bClosed\\b) user connections))\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statisticsMap.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("connections"), m.group("packets"));
            }
        }
    }

    private void getSessManSessions(List<String> inputList){
        String pattern = SERVICE + "/(?<sessions>((\\bOpen\\b|\\bMaximum\\b|\\bTotal\\b) user sessions))\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statisticsMap.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("sessions"), m.group("packets"));
            }
        }
    }

//    private void getSessManProcessor(List<String> inputList){
//        String pattern = SERVICE + SESSMAN_PROCESSOR_REGEX;
//        Pattern r = Pattern.compile(pattern);
//        for(String line : inputList){
//            Matcher m = r.matcher(line);
//            if(m.find()){
//                statisticsMap.put(m.group(SERVICE_NAME_STRING) + "/Processor: " + m.group("parameter"), m.group("value"));
//            }
//        }
//    }

    private void getHighSessManProcessor(List<String> inputList){
        String pattern = SERVICE + SESSMAN_PROCESSOR_REGEX;
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                if(Long.parseLong(m.group("lostValue")) > 0 || Long.parseLong(m.group("queueValue")) > 0){
                    statisticsMap.put(m.group(SERVICE_NAME_STRING) + "/Processor: " + m.group("parameter"), m.group("value"));
                }
             }
        }
    }

    private void getQueues(List<String> inputList){
        String pattern = SERVICE + "/(?<parameter>((IN|OUT)_QUEUE ((IQ [A-Za-z0-9/._#:-]+)|IQ|other|cluster|presences|messages|IQ no XMLNS)))\\s+(?<value>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statisticsMap.put(m.group(SERVICE_NAME_STRING) + "/" + m.group("parameter"), m.group("value"));
            }
        }
    }

    private void getServiceComponentQueues(List<String> inputList, String serviceComponentName){
        String pattern = "(?<serviceComponentName>" + serviceComponentName +")" + "/(?<parameter>((IN|OUT)_QUEUE ((IQ [A-Za-z0-9/._#:-]+)|IQ|other|cluster|presences|messages|IQ no XMLNS)))\\s+(?<value>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statisticsMap.put(m.group("serviceComponentName") + "/" + m.group("parameter"), m.group("value"));
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

    @Override
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

    //Something wrong with the builder(fix later)
//    protected StatisticData createStatisticDataObject(Map inputMap, LocalDateTime time){
//        return new StatisticDataBuilder().setTime(time).setServerStatistic("c2s/Last hour packets").setValue((Long)inputMap.get("c2s/Last hour packets")).createStatisticData();
//    }

//    protected StatisticData createStatisticDataObject(Map inputMap, LocalDateTime time){
//        StatisticData statisticDataObject = new StatisticData(time,"c2s/Last hour packets", (Long) inputMap.get("c2s/Last hour packets"));
//        return statisticDataObject;
//    }

    protected void createStatisticDataObjectList(Map<String, String> inputMap, LocalDateTime time){
        for (Map.Entry<String, String> entry : inputMap.entrySet()) {
            StatisticData statisticDataObject = new StatisticData(time, entry.getKey(),entry.getValue());
            statisticDataArrayList.add(statisticDataObject);
        }
    }

    @Override
    protected Map<String, Object> analyzeLog(InputStream inputStream, String logFileName){
        List<String> statsList = getStatsValues(inputStream);
        getStatisticsFromList(statsList);
        createStatisticDataObjectList(statisticsMap, extractLocalDateTimeFromFilename(logFileName));
        SortedMap<String,Object > testmap = createResultsMapFromStatisticDataObject(statisticDataArrayList);
        getServerStatisticsNames(statisticDataArrayList);
        return testmap;
    }

    private void getServerStatisticsNames(List<StatisticData> statisticDataObjectsList){
        for(StatisticData statisticData: statisticDataObjectsList){
            serverStatisticsNames.add(statisticData.getServerStatisticName());
        }
    }

}
