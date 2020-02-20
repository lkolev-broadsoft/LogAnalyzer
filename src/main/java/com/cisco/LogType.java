package com.cisco;

public enum LogType {
    GATEWAY_LOG ("GateWayLog"),
    DB_CONNECTOR_LOG ("dbConnectorLog"),
    PRESENCE_LOG ("PresenceLog"),
    PROVISIONING_ADAPTER_LOG ("ProvisioningAdapterLog"),
    STATS_LOG ("stats");

    private final String logTypeName;

    LogType(final String name){
        this.logTypeName = name;
    }

    public String getName() {
        return logTypeName;
    }
}
