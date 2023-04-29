package com.glackfag.goalmate.statistics;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.PieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChartFormer {
    private final DatasetFormer datasetFormer;

    @Autowired
    public ChartFormer(DatasetFormer datasetFormer) {
        this.datasetFormer = datasetFormer;
    }

    public JFreeChart formPieChart(Long userId){
        PieDataset<String> dataset = datasetFormer.formPieDatasetByUserId(userId);

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
