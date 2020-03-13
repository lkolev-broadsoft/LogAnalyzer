package com.cisco;


import java.time.LocalDateTime;

public class SessManStatisticData extends StatisticData {

    protected String parameter;
    protected Long queue;
    protected Long averageTime;
    protected Long runs;
    protected Long lost;

    public SessManStatisticData(LocalDateTime time, String serverStatistic) {
        super(time, serverStatistic);
    }

    public SessManStatisticData(LocalDateTime time) {
        super(time);
    }

    public SessManStatisticData(LocalDateTime time, String serverStatistic, String parameter, Long queue, Long averageTime, Long runs, Long lost) {
        super(time, serverStatistic);
        this.parameter = parameter;
        this.averageTime = averageTime;
        this.runs = runs;
        this.lost = lost;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Long getQueue() {
        return queue;
    }

    public void setQueue(Long queue) {
        this.queue = queue;
    }

    public Long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(Long averageTime) {
        this.averageTime = averageTime;
    }

    public Long getRuns() {
        return runs;
    }

    public void setRuns(Long runs) {
        this.runs = runs;
    }

    public Long getLost() {
        return lost;
    }

    public void setLost(Long lost) {
        this.lost = lost;
    }
}
