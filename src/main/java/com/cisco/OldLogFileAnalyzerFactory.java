package com.cisco;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OldLogFileAnalyzerFactory {

    protected static final String IMP_LOG = "IMPLog";
    protected static final String GATEWAY_LOG = "GateWayLog";
    protected static final String DB_CONNECTOR_LOG = "dbConnectorLog";
    protected static final String PRESENCE_LOG = "PresenceLog";
    protected static final String PROVISIONING_ADAPTER_LOG = "ProvisioningAdapterLog";
    protected static final String STATS_LOG = "stats";

    public LogFileAnalyzer getLogFileAnalyzer(String logFileName){
        String logFileType = getLogType(logFileName);
        if(logFileType == null){
            return null;
        }
        if(logFileType.equalsIgnoreCase(IMP_LOG)){
            return new IMPLogAnalyzer();
        }
        else if(logFileType.equalsIgnoreCase(GATEWAY_LOG)){
            return new GateWayLogAnalyzer();
        }
        else if(logFileType.equalsIgnoreCase(DB_CONNECTOR_LOG)){
            return new DBConnectorLogAnalyzer();
        }
        else if(logFileType.equalsIgnoreCase(PRESENCE_LOG)){
            return new PresenceLogAnalyzer();
        }
        else if(logFileType.equalsIgnoreCase(PROVISIONING_ADAPTER_LOG)){
            return new ProvisioningAdapterLogAnalyzer();
        }
        else if(logFileType.equalsIgnoreCase(STATS_LOG)){
            return new StatsLogAnalyzer();
        }
        else{
            return null;
        }
    }

    public String getLogType(String logFileName){
        String logFileType = "";
        String pattern = "(IMPLog|GateWayLog|dbConnectorLog|PresenceLog|ProvisioningAdapterLog|stats)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(logFileName);
        if(m.find()){
            logFileType = m.group();
        }
        return logFileType;
    }

}
