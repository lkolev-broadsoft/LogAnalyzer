package com.cisco;

import java.time.LocalDateTime;

public class ImpStatisticsData extends StatisticData{

    public ImpStatisticsData(LocalDateTime time, String serverStatistic, String value) {
        super(time, serverStatistic, value);
    }
}
