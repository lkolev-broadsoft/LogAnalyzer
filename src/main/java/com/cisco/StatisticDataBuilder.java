package com.cisco;

import java.time.LocalDateTime;

public class StatisticDataBuilder {

    private LocalDateTime time;
    private String serverStatistic;
    private Long value;
    private Long normalValue;

    public StatisticDataBuilder setTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    public StatisticDataBuilder setServerStatistic(String serverStatistic) {
        this.serverStatistic = serverStatistic;
        return this;
    }

    public StatisticDataBuilder setValue(Long value) {
        this.value = value;
        return this;
    }

    public StatisticData createStatisticData() {
        return new StatisticData(time);
    }
}