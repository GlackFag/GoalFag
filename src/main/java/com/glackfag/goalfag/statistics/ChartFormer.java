package com.glackfag.goalfag.statistics;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.PieDataset;
import org.springframework.stereotype.Component;

@Component
public class ChartFormer {
    public JFreeChart formPieChartFromDataset(PieDataset<String> dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Goals state", // chart title
                dataset, // data
                true, // include legend
                false, // tooltips
                false // urls
        );

        chart.setBackgroundPaint(null);
        chart.setBorderVisible(false);

        return chart;
    }
}
