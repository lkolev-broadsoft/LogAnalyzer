package com.cisco;


public class LogFileAnalyzerFactory {

    protected static final String IMP_LOG = "IMPLog";

    protected static final String GATEWAY_LOG = "GateWayLog";
    protected static final String DB_CONNECTOR_LOG = "dbConnectorLog";
    protected static final String PRESENCE_LOG = "PresenceLog";
    protected static final String PROVISIONING_ADAPTER_LOG = "ProvisioningAdapterLog";
    protected static final String STATS_LOG = "stats";

    public LogFileAnalyzer getLogFileAnalyzer(String logType){
        if(logType == null){
            return null;
        }
        if(logType.equalsIgnoreCase(IMP_LOG)){
            return new IMPLogAnalyzer();
        }
        else if(logType.equalsIgnoreCase(GATEWAY_LOG)){
            return new GateWayLogAnalyzer();
        }
        else if(logType.equalsIgnoreCase(DB_CONNECTOR_LOG)){
            return new DBConnectorLogAnalyzer();
        }
        else if(logType.equalsIgnoreCase(PRESENCE_LOG)){
            return new PresenceLogAnalyzer();
        }
        else if(logType.equalsIgnoreCase(PROVISIONING_ADAPTER_LOG)){
            return new ProvisioningAdapterLogAnalyzer();
        }
        else if(logType.equalsIgnoreCase(STATS_LOG)){
            return new StatsLogAnalyzer();
        }
        else{
            return null;
        }
    }

}
