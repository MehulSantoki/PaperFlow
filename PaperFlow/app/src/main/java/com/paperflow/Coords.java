package com.paperflow;

import org.opencv.core.Mat;

public class Coords {
    private int[] coords,xrange;
    private int que,sub_part;
    private String type,value,response;
    private Mat roi;

    public Coords() {
    }

    public Coords(int[] coords, int que, int sub_part, String type, String value, Mat roi,String response,int[] xrange) {
        this.coords = coords;
        this.que = que;
        this.sub_part = sub_part;
        this.type = type;
        this.value = value;
        this.roi = roi;
        this.response = response;
        this.xrange=xrange;
    }

    public int[] getXrange() {
        return xrange;
    }

    public void setXrange(int[] xrange) {
        this.xrange = xrange;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int[] getCoords() {
        return coords;
    }

    public void setCoords(int[] coords) {
        this.coords = coords;
    }

    public int getQue() {
        return que;
    }

    public void setQue(int que) {
        this.que = que;
    }

    public int getSub_part() {
        return sub_part;
    }

    public void setSub_part(int sub_part) {
        this.sub_part = sub_part;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Mat getRoi() {
        return roi;
    }

    public void setRoi(Mat roi) {
        this.roi = roi;
    }
}
