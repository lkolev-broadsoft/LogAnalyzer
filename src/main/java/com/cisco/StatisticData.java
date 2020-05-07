package com.cisco;

import java.time.LocalDateTime;

public class StatisticData {

    protected LocalDateTime time;
    protected String serverStatisticName;
    protected String value;
    protected String normalValue;

    public StatisticData(LocalDateTime time, String serverStatistic, Long value, Long normalValue){
    }

    public StatisticData(LocalDateTime time) {
        this.time = time;
    }

    public StatisticData(LocalDateTime time, String serverStatistic, String value) {
        this.time = time;
        this.serverStatisticName = serverStatistic;
        this.value = value;
    }

    public StatisticData(LocalDateTime time, String serverStatistic) {
        this.time = time;
        this.serverStatisticName = serverStatistic;
    }


    //Getters and Setters

    public LocalDateTime getTime(){
        return time;
    }

    public String getServerStatisticName(){
        return serverStatisticName;
    }

    public String getValue() {
        return value;
    }

    public String getNormalValue() {
        return normalValue;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setServerStatisticName(String serverStatisticName) {
        this.serverStatisticName = serverStatisticName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setNormalValue(String normalValue) {
        this.normalValue = normalValue;
    }

    @Override
    public String toString() {
        return "StatisticData{" +
                "time=" + time +
                ", serverStatisticName='" + serverStatisticName + '\'' +
                ", value=" + value +
                ", normalValue=" + normalValue +
                '}';
    }
}
