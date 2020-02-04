package com.cisco;

public enum ServiceComponent {
    MESSAGE_ROUTER("message-router"),
    C2S("c2s"),
    S2S("s2s"),
    SESS_MAN ("sess-man");

    private final String service;

    ServiceComponent(String serviceName) {
        this.service = serviceName;
    }

    public String getServiceName() {
        return service;
    }

}
