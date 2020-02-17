package com.cisco;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatsLogAnalyzer  extends  LogFileAnalyzer{

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

    protected SortedMap<String, String> statValuesMap = new TreeMap<>();


    public StatsLogAnalyzer(){
        this.logType = "stats";
    }

//    protected SortedMap<String, Long> getLastPackets(InputStream inputStream){
//        String pattern = service + "\\/(?<lastPackets>Last (?<timeRange>\\bminute\\b|\\bsecond\\b|\\bhour\\b) packets)\\s+(?<packets>\\d+)";
//        SortedMap<String ,Long> lastMinuteStats = new TreeMap<>();
//        Pattern r = Pattern.compile(pattern);
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                Matcher m = r.matcher(line);
//                if(m.find()){
//                    lastMinuteStats.put(m.group("serviceName") + "/" + m.group("lastPackets") ,Long.parseLong(m.group("packets")));
//                }
//            }
//            return lastMinuteStats;
//        } catch (Exception e) {
//            System.err.format("Exception occurred trying to read '%s'.", inputStream);
//            e.printStackTrace();
//            return Collections.emptySortedMap();
//        }
//    }



    protected List<String> getStatsValues(InputStream inputStream){
        List<String> statValues = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                    statValues.add(line);
                }
            } catch (IOException e) {
            e.printStackTrace();
        }
        return statValues;
    }

    protected void getLastPackets(List<String> inputList){
        String pattern = SERVICE + "\\/(?<lastPackets>Last (?<timeRange>\\bminute\\b|\\bsecond\\b|\\bhour\\b) packets)\\s+(?<packets>\\d+)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statValuesMap.put(m.group("serviceName") + "/" + m.group("lastPackets") ,(m.group("packets")));
            }
        }
    }

    protected void getOverflows(List<String> inputList){
        String pattern = SERVICE + "\\/((?<overflowType>\\bSocket\\b|\\bIN Queue\\b|\\bOUT Queue\\b|\\bTotal queues\\b) overflow)\\s+(?<packets>\\d+)";
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
        String pattern = SERVICE + "\\/(?<usageType>(\\bCPU\\b|\\bHEAP\\b|\\bNONHEAP\\b) usage) (.*)";
        Pattern r = Pattern.compile(pattern);
        for(String line : inputList){
            Matcher m = r.matcher(line);
            if(m.find()){
                statValuesMap.put(m.group("serviceName") + "/" + m.group("usageType"), m.group(4));
            }
        }
    }

    protected Map<String, String> analyzeLog(InputStream inputStream){
        List<String> statsList = getStatsValues(inputStream);
        getLastPackets(statsList);
        getOverflows(statsList);
        getSessManProcessor(statsList);
        getCPUusage(statsList);
        return statValuesMap;
    }


}
