package com.glackfag.goalfag.statistics;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChartFormer {
    public PieChart formPieChartFromDataset(Map<String, Integer> dataset) {
        PieChart chart = new PieChartBuilder().width(400).height(300).title("Goals state").build();

        for (Map.Entry<String, Integer> entry : dataset.entrySet())
            chart.addSeries(entry.getKey(), entry.getValue());

        return chart;
    }
}
