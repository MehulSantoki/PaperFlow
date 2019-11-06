package com.paperflow;

import org.opencv.core.Mat;

public class ArucoProps {
    private String identifier;
    private Mat warped;

    public ArucoProps() {
    }

    public ArucoProps(String identifier, Mat warped) {
        this.identifier = identifier;
        this.warped = warped;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Mat getWarped() {
        return warped;
    }

    public void setWarped(Mat warped) {
        this.warped = warped;
    }


}
