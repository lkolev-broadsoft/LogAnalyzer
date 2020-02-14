package com.cisco;


public class LogFileAnalyzerFactory {

    public LogFileAnalyzer getLogFileAnalyzer(String logType){
        if(logType == null){
            return null;
        }
        if(logType.equalsIgnoreCase("IMPLog")){
            return new IMPLogAnalyzer();
        }
        else if(logType.equalsIgnoreCase("GateWayLog")){
            return new GateWayLogAnalyzer();
        }
        else if(logType.equalsIgnoreCase("dbConnectorLog")){
            return new DBConnectorLogAnalyzer();
        }
        else if(logType.equalsIgnoreCase("PresenceLog")){
            return new PresenceLogAnalyzer();
        }
        else if(logType.equalsIgnoreCase("ProvisioningAdapterLog")){
            return new ProvisioningAdapterLogAnalyzer();
        }
        else if(logType.equalsIgnoreCase("stats")){
            return new StatsLogAnalyzer();
        }
        else{
            return null;
        }
    }

}
