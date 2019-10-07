package com.taslabs.chartvision;

import java.util.Arrays;

public class BarChartObj {

    private String Title;
    private String xLabel;
    private String yLabel;
    private String[] labels;
    private double[] values;


    public BarChartObj(String title, String xLabel, String yLabel, String[] labels, double[] values) {
        Title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.labels = labels;
        this.values = values;
    }

    public void printChart(){
        System.out.println("Título: " + this.Title);
        System.out.println("Label Eixo X: " + this.xLabel);
        System.out.println("Label Eixo Y: " + this.yLabel);
        System.out.println("Nomes: " + Arrays.toString(this.labels));
        System.out.println("Valores: " + Arrays.toString(this.values));
    }


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getxLabel() {
        return xLabel;
    }

    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public String getyLabel() {
        return yLabel;
    }

    public void setyLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }
}
