package com.cisco;

import java.time.LocalDateTime;

public class StatisticData {

    protected LocalDateTime time;
    protected String serverStatisticName;
    protected Long value;
    protected Long normalValue;

    public StatisticData(LocalDateTime time, String serverStatistic, Long value, Long normalValue){
    }

    public StatisticData(LocalDateTime time) {
        this.time = time;
    }

    public StatisticData(LocalDateTime time, String serverStatistic, Long value) {
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

    public Long getValue() {
        return value;
    }

    public Long getNormalValue() {
        return normalValue;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setServerStatisticName(String serverStatisticName) {
        this.serverStatisticName = serverStatisticName;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public void setNormalValue(Long normalValue) {
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
