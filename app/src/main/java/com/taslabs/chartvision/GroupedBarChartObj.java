package com.taslabs.chartvision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupedBarChartObj {

    private String Title;
    private String xLabel;
    private String yLabel;
    private List<String> series = new ArrayList<String>();
    private List<String> groups = new ArrayList<String>();
    private List<List> values = new ArrayList<>();

    public GroupedBarChartObj(String title, String xLabel, String yLabel, List<String> series, List<String> groups, List<List> values) {
        Title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.series = series;
        this.groups = groups;
        this.values = values;
    }

    public void printChart(){
        System.out.println("Título: " + this.Title);
        System.out.println("Label Eixo X: " + this.xLabel);
        System.out.println("Label Eixo Y: " + this.yLabel);
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

    public List<String> getSeries() {
        return series;
    }

    public void setSeries(List<String> series) {
        this.series = series;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<List> getValues() {
        return values;
    }

    public void setValues(List<List> values) {
        this.values = values;
    }
}